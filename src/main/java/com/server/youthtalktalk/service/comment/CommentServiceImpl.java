package com.server.youthtalktalk.service.comment;

import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.domain.comment.PolicyComment;
import com.server.youthtalktalk.domain.comment.PostComment;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.domain.post.Post;
import com.server.youthtalktalk.dto.comment.CommentDto;
import com.server.youthtalktalk.dto.comment.CommentTypeDto;
import com.server.youthtalktalk.global.response.exception.InvalidValueException;
import com.server.youthtalktalk.global.response.exception.comment.CommentTypeException;
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
import java.util.Optional;
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
        log.info("createPolicyComment policyId={}, content={}", policyComment.getPolicy().getPolicyId(), policyComment.getContent());
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
    public List<CommentDto> getPolicyComments(String policyId) {
        List<PolicyComment> policyComments = commentRepository.findPolicyCommentsByPolicyIdOrderByCreatedAtAsc(policyId);
        return policyComments.stream()
                .map(comment -> {
                    String nickname = (comment.getWriter() != null) ? comment.getWriter().getNickname() : "null";
                    return new CommentDto(nickname, comment.getContent());
                })
                .collect(Collectors.toList());
    }


    /**
     * 게시글 댓글 조회
     */
    @Override
    public List<CommentDto> getPostComments(Long postId) {
        List<PostComment> postComments = commentRepository.findPostCommentsByPostIdOrderByCreatedAtAsc(postId);
        return postComments.stream()
                .map(comment -> {
                    String nickname = (comment.getWriter() != null) ? comment.getWriter().getNickname() : "null";
                    return new CommentDto(nickname, comment.getContent());
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean validateCommentType(String policyId, Long postId) {
        if (policyId != null && postId == null) { // policy 댓글인 경우
            return true;
        } else if (policyId == null && postId != null) { // post 댓글인 경우
            return false;
        } else {
            throw new CommentTypeException();
        }
    }
}
