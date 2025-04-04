package com.server.youthtalktalk.domain.post.dto;

import com.server.youthtalktalk.domain.post.entity.Content;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostCreateTestReqDto {
    @NotBlank(message = "게시글 제목은 필수값입니다.")
    @Size(max = 50, message = "게시글 제목은 최대 50자입니다.")
    private String title;

    @NotNull(message = "게시글 본문 리스트는 필수값입니다.")
    @Size(min = 1, message = "게시글 본문 리스트는 최소 1개 이상입니다.")
    private List<Content> contentList;

    private String postType; // 자유글 : null, 리뷰 : review
    private String policyId; // 자유글 : null, 리뷰 : 해당 정책 아이디
}
