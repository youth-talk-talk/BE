package com.server.youthtalktalk.domain.post.dto;

import com.server.youthtalktalk.domain.post.entity.Content;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

public record PostUpdateReqDto(
        @NotBlank(message = "게시글 제목은 필수값입니다.")
        @Size(max = 50, message = "게시글 제목은 최대 50자입니다.")
        String title,

        String postType,

        @NotNull(message = "게시글 본문은 필수값입니다.")
        @Size(min = 1, message = "게시글 본문은 필수값입니다.")
        List<Content> contentList,

        Long policyId,                  // 정책을 변경했을 때만
        List<String> addImgUrlList,
        List<String> deletedImgUrlList
) {}
