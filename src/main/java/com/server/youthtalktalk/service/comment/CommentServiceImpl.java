package com.server.youthtalktalk.service.comment;

import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.dto.comment.CommentDto;
import com.server.youthtalktalk.dto.comment.CommentTypeDto;
import com.server.youthtalktalk.global.response.exception.InvalidValueException;
import com.server.youthtalktalk.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
