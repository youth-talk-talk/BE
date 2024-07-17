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
    public CommentDto createPolicyComment(String policyId, String content, Member member) {
        Policy policy = policyRepository.findById(policyId).orElseThrow(PolicyNotFoundException::new);
        PolicyComment comment = PolicyComment.builder().content(content).build();
        comment.setPolicy(policy);
        comment.setWriter(member);
        commentRepository.save(comment);
        return new CommentDto(comment.getWriter().getNickname(), comment.getContent());
    }

    /**
     * 게시글 댓글 생성
     */
    @Override
    public CommentDto createPostComment(Long postId, String content, Member member) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        PostComment comment = PostComment.builder().content(content).build();
        comment.setPost(post);
        comment.setWriter(member);
        commentRepository.save(comment);
        return new CommentDto(comment.getWriter().getNickname(), comment.getContent());
    }

    /**
     * 댓글 조회
     */
    @Override
    public List<Comment> getAllComments(CommentTypeDto commentTypeDto) {
        Optional<String> policyId = commentTypeDto.policyId();
        Optional<Long> postId = commentTypeDto.postId();

        if (policyId.isPresent() && postId.isPresent())
            throw new InvalidValueException(INVALID_INPUT_VALUE);

        return policyId
                .map(commentRepository::findPolicyCommentsByPolicyIdOrderByCreatedAtAsc)
                .orElseGet(() -> postId
                        .map(commentRepository::findPostCommentsByPostIdOrderByCreatedAtAsc)
                        .orElseThrow(() -> new InvalidValueException(INVALID_INPUT_VALUE)));

    }

    /**
     * 작성자 없는 댓글 처리 및 dto로 변환
     */
    @Override
    public List<CommentDto> convertToDto(List<Comment> comments) {
        return comments.stream()
                .map(comment -> {
                    String nickname = (comment.getWriter() != null) ? comment.getWriter().getNickname() : "null";
                    return new CommentDto(nickname, comment.getContent());
                })
                .collect(Collectors.toList());
    }
}
