package com.server.youthtalktalk.dto.post;

import com.server.youthtalktalk.domain.post.Content;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostCreateTestReqDto {
    @NotBlank(message = "게시글 제목은 필수값입니다.")
    @Size(max = 50,message = "게시글 제목은 최대 50자입니다.")
    private String title;

    @NotNull(message = "Content list cannot be null")
    @Size(min = 1, message = "Content list must contain at least one item")
    private List<Content> contentList;

    private String postType; // 자유글 : null, 리뷰 : review
    private String policyId; // 자유글 : null, 리뷰 : 해당 정책 아이디
}
