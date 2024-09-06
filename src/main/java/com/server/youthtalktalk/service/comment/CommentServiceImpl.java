package com.server.youthtalktalk.service.comment;

import com.server.youthtalktalk.domain.Likes;
import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.domain.comment.PolicyComment;
import com.server.youthtalktalk.domain.comment.PostComment;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.domain.post.Post;
import com.server.youthtalktalk.dto.comment.CommentDto;
import com.server.youthtalktalk.dto.comment.MyCommentDto;
import com.server.youthtalktalk.global.response.exception.InvalidValueException;
import com.server.youthtalktalk.global.response.exception.comment.AlreadyLikedException;
import com.server.youthtalktalk.global.response.exception.comment.CommentLikeNotFoundException;
import com.server.youthtalktalk.global.response.exception.comment.CommentNotFoundException;
import com.server.youthtalktalk.global.response.exception.policy.PolicyNotFoundException;
import com.server.youthtalktalk.global.response.exception.post.PostNotFoundException;
import com.server.youthtalktalk.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final CommentRepository commentRepository;
    private final PolicyRepository policyRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;

    /**
     * 정책 댓글 생성
     */
    @Override
    public PolicyComment createPolicyComment(String policyId, String content, Member member) {
        Policy policy = policyRepository.findById(policyId).orElseThrow(PolicyNotFoundException::new);
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
        return postComment;
    }

    /**
     * 정책 댓글 조회
     */
    @Override
    public List<PolicyComment> getPolicyComments(String policyId) {
        if (policyId == null || policyId.trim().isEmpty()) {
            throw new InvalidValueException(INVALID_INPUT_VALUE);
        }
        policyRepository.findById(policyId).orElseThrow(PolicyNotFoundException::new);
        return commentRepository.findPolicyCommentsByPolicyIdOrderByCreatedAtAsc(policyId);
    }

    /**
     * 게시글 댓글 조회
     */
    @Override
    public List<PostComment> getPostComments(Long postId) {
        if (postId == null || postId < 0) {
            throw new InvalidValueException(INVALID_INPUT_VALUE);
        }
        postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        return commentRepository.findPostCommentsByPostIdOrderByCreatedAtAsc(postId);
    }

    /**
     * 회원이 작성한 댓글 조회
     */
    @Override
    public List<Comment> getMyComments(Member member) {
        return commentRepository.findCommentsByWriterOrderByCreatedAtDesc(member);
    }

    /**
     * 회원이 좋아요한 댓글 조회
     */
    @Override
    public List<Comment> getLikedComments(Member member) {
        return likeRepository.findAllByMemberOrderByCreatedAtDesc(member)
                .stream().map(Likes::getComment).collect(Collectors.toList());
    }

    /**
     * CommentDto로 변환
     */
    @Override
    public List<CommentDto> toCommentDtoList(List<? extends Comment> comments, Member member) {
        return comments.stream()
                .map(comment -> {
                    String writerNickname = (comment.getWriter() != null) ? comment.getWriter().getNickname() : "null"; // writer null인 경우 닉네임 치환
                    boolean isLikedByMember = isLikedByMember(comment, member); // 회원의 좋아요 여부 판단
                    Object relatedEntityId = comment.getRelatedEntityId(); // 연관 엔티티(post 또는 policy)의 id
                    return new CommentDto(comment.getId(), writerNickname, comment.getContent(), isLikedByMember, relatedEntityId);
                })
                .collect(Collectors.toList());
    }

    /**
     * MyCommentDto로 변환 (좋아요 없음, 닉네임 고정)
     */
    @Override
    public List<MyCommentDto> toMyCommentDtoList(List<Comment> comments, String nickname) {
        return comments.stream()
                .map(comment -> {
                    Object relatedEntityId = comment.getRelatedEntityId(); // 연관 엔티티(post 또는 policy)의 id
                    return new MyCommentDto(comment.getId(), nickname, comment.getContent(), relatedEntityId);
                })
                .collect(Collectors.toList());
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

}
