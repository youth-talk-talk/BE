package com.server.youthtalktalk.domain.announcement.dto;

import com.server.youthtalktalk.domain.image.entity.AnnouncementImage;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AnnouncementRepDto {
    private Long id;
    private String title;
    private String content;
    private List<AnnouncementImage> imageList;
    private String updateAt;
}
