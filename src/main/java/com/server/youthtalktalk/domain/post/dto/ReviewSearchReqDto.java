package com.server.youthtalktalk.domain.post.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ReviewSearchReqDto {
    private List<String> categories;
}
