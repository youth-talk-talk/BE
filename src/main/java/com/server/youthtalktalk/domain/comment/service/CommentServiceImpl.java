package com.server.youthtalktalk.domain.comment.service;

import com.server.youthtalktalk.domain.comment.dto.CommentDto;
import com.server.youthtalktalk.domain.comment.dto.LikeCommentDto;
import com.server.youthtalktalk.domain.comment.dto.MyCommentDto;
import com.server.youthtalktalk.domain.comment.entity.Comment;
import com.server.youthtalktalk.domain.comment.entity.PolicyComment;
import com.server.youthtalktalk.domain.comment.entity.PostComment;
import com.server.youthtalktalk.domain.comment.repository.CommentRepository;
import com.server.youthtalktalk.domain.likes.entity.Likes;
import com.server.youthtalktalk.domain.likes.repository.LikeRepository;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.repository.BlockRepository;
import com.server.youthtalktalk.domain.member.repository.MemberRepository;
import com.server.youthtalktalk.domain.notification.entity.NotificationDetail;
import com.server.youthtalktalk.domain.notification.entity.NotificationType;
import com.server.youthtalktalk.domain.notification.entity.SSEEvent;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.repository.PolicyRepository;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.post.repostiory.PostRepository;
import com.server.youthtalktalk.domain.report.entity.CommentReport;
import com.server.youthtalktalk.domain.report.repository.ReportRepository;
import com.server.youthtalktalk.global.response.exception.InvalidValueException;
import com.server.youthtalktalk.global.response.exception.comment.AlreadyLikedException;
import com.server.youthtalktalk.global.response.exception.comment.CommentLikeNotFoundException;
import com.server.youthtalktalk.global.response.exception.comment.CommentNotFoundException;
import com.server.youthtalktalk.global.response.exception.policy.PolicyNotFoundException;
import com.server.youthtalktalk.global.response.exception.post.PostNotFoundException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.server.youthtalktalk.global.response.BaseResponseCode.INVALID_INPUT_VALUE;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    public static final String DELETED_WRITER = "알 수 없음";
    public static final String DEFAULT_PROFILE = "기본 이미지";
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final CommentRepository commentRepository;
    private final PolicyRepository policyRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;
    private final BlockRepository blockRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 정책 댓글 생성
     */
    @Override
    public PolicyComment createPolicyComment(Long policyId, String content, Member member) {
        Policy policy = policyRepository.findByPolicyId(policyId).orElseThrow(PolicyNotFoundException::new);
        PolicyComment policyComment = PolicyComment.builder().content(content).build();
        policyComment.setPolicy(policy);
        policyComment.setWriter(member);
        commentRepository.save(policyComment);

        return policyComment;
    }

    /**
     * 게시글 댓글 생성
     */
    @Override
    public PostComment createPostComment(Long postId, String content, Member member) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        PostComment postComment = PostComment.builder().content(content).build();
        postComment.setPost(post);
        postComment.setWriter(member);
        commentRepository.save(postComment);

        // 자신의 게시글에 작성한 댓글이 아니면 알림 전송
        if(post.getWriter() != null && !isEqualsPostWriter(member, post)){
            eventPublisher.publishEvent(SSEEvent.builder()
                    .detail(NotificationDetail.POST_COMMENT)
                    .receiver(post.getWriter())              // 단일 수신자용 @Singular 메서드
                    .sender(member.getNickname())
                    .policyTitle(null)
                    .type(NotificationType.POST)
                    .id(post.getId())
                    .comment(postComment.getContent())
                    .build());
        }
        return postComment;
    }

    /**
     * 정책 댓글 조회 (오래된 순)
     */
    @Override
    public List<PolicyComment> getPolicyComments(Long policyId, Member member) {
        if (!policyRepository.existsByPolicyId(policyId)) {
            throw new PolicyNotFoundException();
        }
        List<PolicyComment> policyComments = commentRepository.findPolicyCommentsByPolicyId(policyId);
        Set<Comment> excludedComments = getExcludedComments(member); // 신고한 댓글 + 차단한 유저의 댓글

        return policyComments.stream()
                .filter(comment -> !excludedComments.contains(comment))
                .sorted(Comparator.comparing(PolicyComment::getCreatedAt)) // 오래된 순 정렬
                .collect(Collectors.toList());
    }

    /**
     * 게시글 댓글 조회 (오래된 순)
     */
    @Override
    public List<PostComment> getPostComments(Long postId, Member member) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException();
        }
        List<PostComment> postComments = commentRepository.findPostCommentsByPostId(postId);
        Set<Comment> excludedComments = getExcludedComments(member); // 신고한 댓글 + 차단한 유저의 댓글

        return postComments.stream()
                .filter(comment -> !excludedComments.contains(comment))
                .sorted(Comparator.comparing(PostComment::getCreatedAt)) // 오래된 순 정렬
                .collect(Collectors.toList());
    }

    public Set<Comment> getExcludedComments(Member member) {
        // 차단한 유저들의 댓글
        List<Comment> blockedComments = blockRepository.findBlockedMembersByBlocker(member).stream()
                .flatMap(blockedMember -> blockedMember.getComments().stream()).toList();

        // 신고한 댓글
        List<Comment> reportedComments = reportRepository.findCommentReportsByReporter(member).stream()
                .map(CommentReport::getComment).toList();

        Set<Comment> excludedComments = new HashSet<>(blockedComments); // 중복 제거
        excludedComments.addAll(reportedComments);
        return excludedComments;
    }

    /**
     * 회원이 작성한 댓글 조회 (최신순)
     */
    @Override
    public List<Comment> getMyComments(Member member) {
        return commentRepository.findCommentsByWriterOrderByCreatedAtDesc(member);
    }

    /**
     * 회원이 좋아요한 댓글 조회 (최신순)
     */
    @Override
    public List<Comment> getLikedComments(Member member) {
        return likeRepository.findAllByMemberOrderByCreatedAtDesc(member)
                .stream().map(Likes::getComment).toList();
    }

    /**
     * 게시글/정책 댓글 조회용 DTO 리스트로 변환
     */
    @Override
    public List<CommentDto> toCommentDtoList(List<? extends Comment> comments, Member member) {
        return comments.stream()
                .map(comment -> {
                    Member writer = comment.getWriter();
                    Long writerId = (writer == null) ? -1L : writer.getId();
                    String nickname = (writer == null) ? DELETED_WRITER : writer.getNickname();
                    String profileImg = (writer == null || writer.getProfileImage() == null)
                            ? null : writer.getProfileImage().getImgUrl();
                    String content = comment.getContent();
                    String createdAt = comment.getCreatedAt()
                            .format(DateTimeFormatter.ofPattern(TIME_FORMAT));
                    Boolean isLikedByMember = isLikedByMember(comment, member);
                    return new CommentDto(comment.getId(), writerId, nickname, profileImg, content, isLikedByMember, createdAt);
                })
                .toList();
    }

    /**
     * 내가 작성한 댓글 조회용 DTO 리스트로 변환
     */
    @Override
    public List<MyCommentDto> toMyCommentDtoList(List<Comment> comments, Member member) {
        return comments.stream()
                .map(comment -> {
                    Long commentId = comment.getId();
                    String content = comment.getContent();
                    Long articleId = comment.getArticleId();
                    String articleType = comment.getArticleType();
                    String articleTitle = comment.getArticleTitle();
                    Boolean isLikedByMember = isLikedByMember(comment, member);
                    int likeCount = comment.getCommentLikes().size();
                    return new MyCommentDto(commentId, content, articleId,
                            articleType, articleTitle, isLikedByMember, likeCount);
                })
                .toList();
    }

    /**
     * 좋아요한 댓글 조회용 DTO 리스트로 변환
     */
    @Override
    public List<LikeCommentDto> toLikeCommentDtoList(List<Comment> comments, Member member) {
        return comments.stream()
                .map(comment -> {
                    Long commentId = comment.getId();
                    Member writer = comment.getWriter();
                    Long writerId = (writer == null) ? -1L : writer.getId();
                    String nickname = (writer == null) ? DELETED_WRITER : writer.getNickname();
                    String content = comment.getContent();
                    Long articleId = comment.getArticleId();
                    String articleType = comment.getArticleType();
                    String articleTitle = comment.getArticleTitle();
                    Boolean isLikedByMember = isLikedByMember(comment, member);
                    int likeCount = comment.getCommentLikes().size();
                    return new LikeCommentDto(commentId, writerId, nickname, content, articleId,
                            articleType, articleTitle, isLikedByMember, likeCount);
                })
                .toList();
    }

    /**
     * 댓글 수정
     */
    @Override
    public void updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        comment.updateContent(content);
        commentRepository.save(comment);
    }

    /**
     * 댓글 삭제
     */
    @Override
    public void deleteComment(Long commentId) {
        if (commentId == null) {
            throw new InvalidValueException(INVALID_INPUT_VALUE);
        }
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        Member writer = comment.getWriter();
        if (writer != null) {
            writer.getComments().remove(comment);
        }
        commentRepository.delete(comment);
    }

    /**
     * 좋아요 여부 판단
     */
    @Override
    public boolean isLikedByMember(Comment comment, Member member) {
        return comment.getCommentLikes()
                .stream().anyMatch(likes -> likes.getMember().equals(member));
    }

    /**
     * 좋아요 등록
     */
    @Override
    public void setCommentLiked(Long commentId, Member member) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        if (likeRepository.findByMemberAndComment(member, comment).isPresent())
            throw new AlreadyLikedException();

        Likes like = Likes.builder().build();
        like.setComment(comment);
        like.setMember(member);
        likeRepository.save(like);

        // 나의 댓글에 좋아요를 누른게 아니라면 알림 전송
        if(comment.getWriter() != null && !isEqualsCommentWriter(member, comment)){
            eventPublisher.publishEvent(SSEEvent.builder()
                    .detail(
                            isPostComment(comment)
                                    ? NotificationDetail.POST_COMMENT_LIKE
                                    : NotificationDetail.POLICY_COMMENT_LIKE
                    )
                    .receiver(comment.getWriter())    // @Singular("receiver") 덕분에 리스트에 추가됩니다
                    .sender(member.getNickname())
                    .policyTitle(null)
                    .type(isPostComment(comment) ? NotificationType.POST : NotificationType.POLICY)
                    .id(
                            isPostComment(comment)
                                    ? ((PostComment) comment).getPost().getId()
                                    : ((PolicyComment) comment).getPolicy().getPolicyId()
                    )
                    .comment(comment.getContent())
                    .build());
        }
    }

    /**
     * 좋아요 해제
     */
    @Override
    public void setCommentUnliked(Long commentId, Member member) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        Likes like = likeRepository.findByMemberAndComment(member, comment).orElseThrow(CommentLikeNotFoundException::new);
        member.removeLike(like);
        comment.removeLike(like);
        memberRepository.save(member);
        commentRepository.save(comment);
        likeRepository.delete(like);
    }

    private boolean isPostComment(Comment comment) {
        return comment instanceof PostComment;
    }

    private static boolean isEqualsPostWriter(Member member, Post post) {
        return post.getWriter().getId().equals(member.getId());
    }

    private static boolean isEqualsCommentWriter(Member member, Comment comment) {
        return comment.getWriter().getId().equals(member.getId());
    }
    
}
