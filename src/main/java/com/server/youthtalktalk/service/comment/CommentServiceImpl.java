package com.server.youthtalktalk.service.comment;

import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.domain.comment.PolicyComment;
import com.server.youthtalktalk.domain.comment.PostComment;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.domain.post.Post;
import com.server.youthtalktalk.dto.comment.CommentDto;
import com.server.youthtalktalk.global.response.exception.policy.PolicyNotFoundException;
import com.server.youthtalktalk.global.response.exception.post.PostNotFoundException;
import com.server.youthtalktalk.repository.CommentRepository;
import com.server.youthtalktalk.repository.PolicyRepository;
import com.server.youthtalktalk.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PolicyRepository policyRepository;
    private final PostRepository postRepository;

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
        policyRepository.findById(policyId).orElseThrow(PolicyNotFoundException::new);
        return commentRepository.findPolicyCommentsByPolicyIdOrderByCreatedAtAsc(policyId);
    }

    /**
     * 게시글 댓글 조회
     */
    @Override
    public List<PostComment> getPostComments(Long postId) {
        postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        return commentRepository.findPostCommentsByPostIdOrderByCreatedAtAsc(postId);
    }

    /**
     * 작성자 없는 경우 처리 후 CommentDto 리스트로 변환
     */
    @Override
    public List<CommentDto> convertToCommentDtoList(List<? extends Comment> comments) {
        return comments.stream()
                .map(comment -> { // writer null인 경우 닉네임 치환
                    String writerNickname = (comment.getWriter() != null) ? comment.getWriter().getNickname() : "null";
                    return new CommentDto(writerNickname, comment.getContent());
                })
                .collect(Collectors.toList());
    }

}
