package com.server.youthtalktalk.domain.post.dto;

import com.server.youthtalktalk.domain.post.entity.Content;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PostCreateReqDto (
    @NotBlank(message = "게시글 제목은 필수값입니다.")
    @Size(max = 50,message = "게시글 제목은 최대 50자입니다.")
    String title,

    @NotNull(message = "Content list cannot be null")
    @Size(min = 1, message = "Content list must contain at least one item")
    List<Content> contentList,
    String postType, // 자유글 : null, 리뷰 : review
    Long policyId  // 자유글 : null, 리뷰 : 해당 정책 아이디
){
}
