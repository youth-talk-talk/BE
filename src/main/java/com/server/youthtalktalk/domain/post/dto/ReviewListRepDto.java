package com.server.youthtalktalk.domain.post.dto;

import com.server.youthtalktalk.domain.policy.entity.Category;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewListRepDto {

    private List<ReviewListDto> top5Posts;
    private List<ReviewListDto> allPosts;

    @Getter
    @Builder
    public static class ReviewListResponse{
        private long total;
        private int page;
        private List<ReviewListDto> posts;
    }

    @Getter
    @Builder
    public static class ReviewListDto {
        private Long postId;
        private String title;
        private Long writerId;
        private Long policyId;
        private String policyTitle;
        private int comments;
        private String contentPreview;
        private int scrapCount;
        private boolean scrap;
        private Category category;
        private String createdAt;
    }
}
