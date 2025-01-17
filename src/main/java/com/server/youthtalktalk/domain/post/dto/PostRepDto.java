package com.server.youthtalktalk.domain.post.dto;

import com.server.youthtalktalk.domain.post.entity.Content;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PostRepDto {
    private Long postId;
    private String postType;
    private String title;
    private List<Content> contentList;
    private String policyId; // 자유글 null
    private String policyTitle; // 자유글 null
    private Long writerId;
    private String nickname;
    private Long view;
    private List<String> images;
    private String category;
    private boolean isScrap;
}
