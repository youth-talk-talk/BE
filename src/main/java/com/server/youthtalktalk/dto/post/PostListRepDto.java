package com.server.youthtalktalk.dto.post;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostListRepDto {
    private List<PostListDto> top5_posts;
    private List<PostListDto> other_posts;

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
        private String content;
        private Long writerId;
        private int scraps;
        private boolean scrap;
        private int comments;
        private String policyId; // 자유글 null
        private String policyTitle; // 자유글 null
    }
}
