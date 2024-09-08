package com.server.youthtalktalk.dto.post;

import com.server.youthtalktalk.domain.image.Image;
import com.server.youthtalktalk.domain.image.PostImage;
import com.server.youthtalktalk.domain.post.Content;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PostRepDto {
    private Long postId;
    private String postType;
    private String title;
    private String content;
    private List<Content> contentList;
    private String policyId; // 자유글 null
    private String policyTitle; // 자유글 null
    private Long writerId;
    private String nickname;
    private Long view;
    private List<String> images;
    private String category;
}
