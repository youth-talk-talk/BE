package com.server.youthtalktalk.repository.comment;

import static org.assertj.core.api.Assertions.assertThat;

import com.server.youthtalktalk.domain.comment.entity.PolicyComment;
import com.server.youthtalktalk.domain.comment.entity.PostComment;
import com.server.youthtalktalk.domain.comment.repository.CommentRepository;
import com.server.youthtalktalk.domain.policy.entity.InstitutionType;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.entity.RepeatCode;
import com.server.youthtalktalk.domain.policy.entity.condition.Marriage;
import com.server.youthtalktalk.domain.policy.entity.region.Region;
import com.server.youthtalktalk.domain.policy.repository.PolicyRepository;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.post.repostiory.PostRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Test
    @DisplayName("postId로 특정 게시글의 전체 댓글 조회")
    void testFindPostCommentsByPostId() {
        // given
        Post post = postRepository.save(Post.builder().title("post1").build());
        PostComment comment1 = commentRepository.save(PostComment.builder().post(post).content("post comment1").build());
        PostComment comment2 = commentRepository.save(PostComment.builder().post(post).content("post comment2").build());

        // when
        List<PostComment> postComments = commentRepository.findPostCommentsByPostId(post.getId());

        // then
        assertThat(postComments.size()).isEqualTo(2);
        assertThat(postComments).extracting("content").containsExactly("post comment1", "post comment2");
    }

    @Test
    @DisplayName("policyId로 특정 정책의 전체 댓글 조회")
    void testGetPolicyComments() {
        // given
        Policy policy = createPolicy();
        policyRepository.save(policy);
        PolicyComment comment1 = commentRepository.save(PolicyComment.builder().policy(policy).content("policy comment1").build());
        PolicyComment comment2 = commentRepository.save(PolicyComment.builder().policy(policy).content("policy comment2").build());

        // when
        List<PolicyComment> policyComments = commentRepository.findPolicyCommentsByPolicyId(policy.getPolicyId());

        // then
        assertThat(policyComments.size()).isEqualTo(2);
        assertThat(policyComments).extracting("content").containsExactly("policy comment1", "policy comment2");
    }

    private static Policy createPolicy() {
        return Policy.builder()
                .policyNum("policyNum1")
                .region(Region.SEOUL)
                .title("policy1")
                .institutionType(InstitutionType.CENTER)
                .repeatCode(RepeatCode.PERIOD)
                .marriage(Marriage.SINGLE)
                .build();
    }
}
