package com.server.youthtalktalk.dto.post;

import com.server.youthtalktalk.domain.image.Image;
import com.server.youthtalktalk.domain.image.PostImage;
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
    private String policyId; // 자유글 null
    private String policyTitle; // 자유글 null
    private Long writerId;
    private String nickname;
    private Long view;
    private List<PostImage> images;
    private String category;
}
