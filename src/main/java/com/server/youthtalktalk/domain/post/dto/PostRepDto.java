package com.server.youthtalktalk.domain.post.dto;

import com.server.youthtalktalk.domain.post.entity.Content;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class PostRepDto {
    private Long postId;
    private String postType;
    private String title;
    private List<Content> contentList;
    private Long policyId; // 자유글 null
    private String policyTitle; // 자유글 null
    private Long writerId;
    private String nickname;
    private String profileImage;
    private Long view;
    private String category;
    private boolean isScrap;
    private String updatedAt;
}
