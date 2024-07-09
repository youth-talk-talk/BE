package com.server.youthtalktalk.dto.post;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostListRepDto {
    private List<PostDto> top5_posts;
    private List<PostDto> other_posts;

    @Getter
    @Builder
    static class PostDto{
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
