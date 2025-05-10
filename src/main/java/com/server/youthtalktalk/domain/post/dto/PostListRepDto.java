package com.server.youthtalktalk.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostListRepDto {
    private List<PostListDto> top5Posts;
    private List<PostListDto> allPosts;

    @Getter
    @Builder
    public static class PostListResponse{
        private long total;
        private int page;
        private List<PostListDto> posts;
    }

    @Getter
    @Builder
    public static class PostListDto {
        private Long postId;
        private String title;
        private Long writerId;
        private Long policyId; // 자유글 null
        private String policyTitle; // 자유글 null
        private int comments;
        private String contentPreview;
        private int scraps;
        private boolean scrap;
        private String createdAt;
    }

    @Getter
    @Builder
    public static class ScrapPostListDto {
        private Long postId;
        private String title;
        private Long writerId;
        private int scraps;
        private boolean scrap;
        private int comments;
        private Long policyId; // 자유글 null
        private String policyTitle; // 자유글 null
        private Long scrapId;
        private String contentPreview;
    }

}
