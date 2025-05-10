package com.server.youthtalktalk.service.comment;

import static com.server.youthtalktalk.domain.comment.service.CommentServiceImpl.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.server.youthtalktalk.domain.comment.dto.LikeCommentDto;
import com.server.youthtalktalk.domain.comment.dto.MyCommentDto;
import com.server.youthtalktalk.domain.comment.entity.Comment;
import com.server.youthtalktalk.domain.comment.entity.PolicyComment;
import com.server.youthtalktalk.domain.comment.entity.PostComment;
import com.server.youthtalktalk.domain.comment.repository.CommentRepository;
import com.server.youthtalktalk.domain.comment.service.CommentServiceImpl;
import com.server.youthtalktalk.domain.likes.entity.Likes;
import com.server.youthtalktalk.domain.likes.repository.LikeRepository;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.repository.BlockRepository;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.repository.PolicyRepository;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.post.entity.Review;
import com.server.youthtalktalk.domain.post.repostiory.PostRepository;
import com.server.youthtalktalk.domain.report.entity.CommentReport;
import com.server.youthtalktalk.domain.report.repository.ReportRepository;
import com.server.youthtalktalk.global.response.exception.policy.PolicyNotFoundException;
import com.server.youthtalktalk.global.response.exception.post.PostNotFoundException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CommentReadServiceTest {

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    @DisplayName("제외 댓글 조회 시, 사용자가 차단한 유저의 댓글과 신고한 댓글을 반환한다.")
    void testGetExcludedComments() {
        // given
        Member member = mock(Member.class);
        Member blockedUser = mock(Member.class);
        CommentReport report = mock(CommentReport.class);
        Comment blockedComment = mock(Comment.class);
        Comment reportedComment = mock(Comment.class);

        when(blockRepository.findBlockedMembersByBlocker(member)).thenReturn(List.of(blockedUser));
        when(blockedUser.getComments()).thenReturn(List.of(blockedComment));
        when(reportRepository.findCommentReportsByReporter(member)).thenReturn(List.of(report));
        when(report.getComment()).thenReturn(reportedComment);

        // when
        Set<Comment> excludedComments = commentService.getExcludedComments(member);

        // then
        assertThat(excludedComments).containsExactlyInAnyOrder(blockedComment, reportedComment);
    }

    @Test
    @DisplayName("제외 댓글 조회 시, 차단한 댓글과 신고한 댓글이 동일하면 중복은 제거한다.")
    void testGetExcludedCommentsDuplication() {
        // given
        Member member = mock(Member.class);
        Member blockedUser = mock(Member.class);
        CommentReport report = mock(CommentReport.class);
        Comment duplicateComment = mock(Comment.class); // 차단한 유저의 댓글이면서 신고한 댓글인 경우

        when(blockRepository.findBlockedMembersByBlocker(member)).thenReturn(List.of(blockedUser));
        when(blockedUser.getComments()).thenReturn(List.of(duplicateComment));
        when(reportRepository.findCommentReportsByReporter(member)).thenReturn(List.of(report));
        when(report.getComment()).thenReturn(duplicateComment);

        // when
        Set<Comment> excludedComments = commentService.getExcludedComments(member);

        // then
        assertThat(excludedComments).hasSize(1); // 중복 제거되어 하나만 있음
    }

    @Test
    @DisplayName("게시글 댓글 조회 시, 차단/신고 댓글은 제외하고 작성일 오름차순으로 반환한다.")
    void testGetPostCommentsFilteredAndSorted() {
        // given
        Long postId = 1L;
        Member member = mock(Member.class);

        PostComment pc1 = mock(PostComment.class);
        PostComment pc2 = mock(PostComment.class);
        PostComment pc3 = mock(PostComment.class);

        when(pc1.getCreatedAt()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0));
        when(pc2.getCreatedAt()).thenReturn(LocalDateTime.of(2024, 1, 3, 0, 0));
        when(pc3.getCreatedAt()).thenReturn(LocalDateTime.of(2024, 1, 2, 0, 0));

        List<PostComment> allComments = List.of(pc1, pc2, pc3);
        Set<Comment> excludedComments = Set.of(pc2);  // 차단/신고된 댓글은 제외됨

        when(postRepository.existsById(postId)).thenReturn(true);
        when(commentRepository.findPostCommentsByPostId(postId)).thenReturn(allComments);

        // 제외 댓글은 spy로 반환
        CommentServiceImpl commentServiceSpy = Mockito.spy(commentService);
        doReturn(excludedComments).when(commentServiceSpy).getExcludedComments(member);

        // when
        List<PostComment> result = commentServiceSpy.getPostComments(postId, member);

        // then
        assertThat(result).containsExactly(pc1, pc3);  // pc2는 제외되어야 함
        assertThat(result).isSortedAccordingTo(Comparator.comparing(PostComment::getCreatedAt));  // 오래된 순 정렬
    }

    @Test
    @DisplayName("게시글 댓글 조회 시, 존재하지 않는 게시글이면 예외를 발생시킨다.")
    void testGetPostCommentsWhenPostNotFound() {
        // given
        Long postId = 1L;
        Member member = mock(Member.class);

        // 게시글이 존재하지 않는 경우
        when(postRepository.existsById(postId)).thenReturn(false);

        // when & then
        assertThrows(PostNotFoundException.class, () -> commentService.getPostComments(postId, member));
    }

    @Test
    @DisplayName("정책 댓글 조회 시, 차단/신고 댓글은 제외하고 작성일 오름차순으로 반환한다.")
    void testGetPolicyCommentsFilteredAndSorted() {
        // given
        Long policyId = 1L;
        Member member = mock(Member.class);

        PolicyComment pc1 = mock(PolicyComment.class);
        PolicyComment pc2 = mock(PolicyComment.class);
        PolicyComment pc3 = mock(PolicyComment.class);

        when(pc1.getCreatedAt()).thenReturn(LocalDateTime.of(2024, 1, 1, 0, 0));
        when(pc2.getCreatedAt()).thenReturn(LocalDateTime.of(2024, 1, 3, 0, 0));
        when(pc3.getCreatedAt()).thenReturn(LocalDateTime.of(2024, 1, 2, 0, 0));

        List<PolicyComment> allComments = List.of(pc1, pc2, pc3);
        Set<Comment> excludedComments = Set.of(pc2);  // 차단/신고된 댓글은 제외됨

        when(policyRepository.existsByPolicyId(policyId)).thenReturn(true);
        when(commentRepository.findPolicyCommentsByPolicyId(policyId)).thenReturn(allComments);

        // 제외 댓글은 spy로 반환
        CommentServiceImpl commentServiceSpy = Mockito.spy(commentService);
        doReturn(excludedComments).when(commentServiceSpy).getExcludedComments(member);

        // when
        List<PolicyComment> result = commentServiceSpy.getPolicyComments(policyId, member);

        // then
        assertThat(result).containsExactly(pc1, pc3);  // pc2는 제외되어야 함
        assertThat(result).isSortedAccordingTo(Comparator.comparing(PolicyComment::getCreatedAt));  // 오래된 순 정렬
    }

    @Test
    @DisplayName("정책 댓글 조회 시, 존재하지 않는 정책이면 예외를 발생시킨다.")
    void testGetPolicyCommentsWhenPolicyNotFound() {
        // given
        Long policyId = 1L;
        Member member = mock(Member.class);

        // 정책이 존재하지 않는 경우
        when(policyRepository.existsByPolicyId(policyId)).thenReturn(false);

        // when & then
        assertThrows(PolicyNotFoundException.class, () -> commentService.getPolicyComments(policyId, member));
    }

    @Test
    @DisplayName("내 댓글 조회 시, 회원이 작성한 모든 댓글을 최신순으로 정렬하여 반환한다.")
    void testGetMyComments() {
        // given
        Member member = Member.builder().id(1L).username("member1").build();
        Comment comment1 = PostComment.builder().id(1L).content("content1").build();
        Comment comment2 = PostComment.builder().id(2L).content("content2").build();

        comment1.setWriter(member);
        comment2.setWriter(member);

        // 최신순 정렬로 가정 (comment2가 더 나중에 작성됨)
        List<Comment> mockComments = List.of(comment2, comment1);
        Mockito.when(commentRepository.findCommentsByWriterOrderByCreatedAtDesc(member)).thenReturn(mockComments);

        // when
        List<Comment> comments = commentService.getMyComments(member);

        // then
        Assertions.assertThat(comments.size()).isEqualTo(2);
        Assertions.assertThat(comments).containsExactly(comment2, comment1); // 최신순 검증
    }

    @Test
    @DisplayName("toMyCommentDtoList - PostComment인 경우 변환 성공")
    void toMyCommentDtoList_PostComment() {
        // given
        Member member = Member.builder().id(1L).username("member1").build();
        Review review = Review.builder().id(10L).title("리뷰 제목").build();
        PostComment comment = PostComment.builder().id(100L).content("댓글 내용").build();
        comment.setWriter(member);
        comment.setPost(review);

        List<Comment> comments = List.of(comment);

        // when
        List<MyCommentDto> result = commentService.toMyCommentDtoList(comments, member);

        // then
        assertThat(result).hasSize(1);
        MyCommentDto dto = result.get(0);
        assertThat(dto.commentId()).isEqualTo(comment.getId());
        assertThat(dto.content()).isEqualTo(comment.getContent());
        assertThat(dto.articleId()).isEqualTo(review.getId());
        assertThat(dto.articleType()).isEqualTo("review");
        assertThat(dto.articleTitle()).isEqualTo(review.getTitle());
        assertThat(dto.isLikedByMember()).isFalse();
        assertThat(dto.likeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("toMyCommentDtoList - PolicyComment인 경우 변환 성공")
    void toMyCommentDtoList_PolicyComment() {
        // given
        Member member = Member.builder().id(1L).username("member1").build();
        Policy policy = Policy.builder().policyId(1L).build();
        PolicyComment comment = PolicyComment.builder().id(100L).content("댓글 내용").build();
        comment.setWriter(member);
        comment.setPolicy(policy);

        List<Comment> comments = List.of(comment);

        // when
        List<MyCommentDto> result = commentService.toMyCommentDtoList(comments, member);

        // then
        assertThat(result).hasSize(1);
        MyCommentDto dto = result.get(0);
        assertThat(dto.commentId()).isEqualTo(comment.getId());
        assertThat(dto.content()).isEqualTo(comment.getContent());
        assertThat(dto.articleId()).isEqualTo(policy.getPolicyId());
        assertThat(dto.articleType()).isEqualTo("policy");
        assertThat(dto.articleTitle()).isEqualTo(policy.getTitle());
        assertThat(dto.isLikedByMember()).isFalse();
        assertThat(dto.likeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("좋아요한 댓글 조회 시, 회원이 좋아요한 댓글을 최신순으로 반환한다.")
    void testGetLikedComments() {
        // given
        Member member = Member.builder().id(1L).username("member1").build();
        Comment comment1 = PostComment.builder().id(1L).content("content1").build();
        Comment comment2 = PostComment.builder().id(2L).content("content1").build();

        Likes like1 = Likes.builder().comment(comment1).build();
        Likes like2 = Likes.builder().comment(comment2).build();

        like1.setMember(member);
        like1.setComment(comment1);
        like2.setMember(member);
        like2.setComment(comment2);

        // 최신순 정렬로 가정 (comment2가 더 나중에 좋아요됨)
        List<Likes> likesList = List.of(like2, like1);
        Mockito.when(likeRepository.findAllByMemberOrderByCreatedAtDesc(member)).thenReturn(likesList);

        // when
        List<Comment> likedComments = commentService.getLikedComments(member);

        // then
        Assertions.assertThat(likedComments).hasSize(2);
        Assertions.assertThat(likedComments).containsExactly(comment2, comment1); // 최신순 검증
    }

    @Test
    @DisplayName("탈퇴한 사용자의 댓글도 LikeCommentDto로 변환된다")
    void toLikeCommentDtoList_writerIsNull() {
        // given
        Post post = Post.builder().id(1L).title("게시글1").build();
        PostComment comment = PostComment.builder().id(1L).content("내용").build();
        comment.setPost(post);
        comment.setWriter(null);

        List<Comment> comments = List.of(comment);
        Member member = Member.builder().id(2L).build();

        // when
        List<LikeCommentDto> result = commentService.toLikeCommentDtoList(comments, member);

        // then
        assertThat(result).hasSize(1);
        LikeCommentDto dto = result.get(0);

        assertThat(dto.writerId()).isEqualTo(-1L);
        assertThat(dto.nickname()).isEqualTo(DELETED_WRITER);
        assertThat(dto.commentId()).isEqualTo(comment.getId());
        assertThat(dto.content()).isEqualTo(comment.getContent());
    }

}
