package com.server.youthtalktalk.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostCreateReqDto {
    @NotBlank(message = "게시글 제목은 필수값입니다.")
    @Size(max = 20,message = "게시글 제목은 최대 20자입니다.")
    private String title;

    @NotBlank(message = "게시글 본문은 필수값입니다.")
    private String content;

    private String postType; // 자유글 : null, 리뷰 : review
    private String policyId; // 자유글 : null, 리뷰 : 해당 정책 아이디
}
