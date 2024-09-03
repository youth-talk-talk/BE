package com.server.youthtalktalk.dto.announcement;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AnnouncementListRepDto {
    private int pageNum;
    private int pageSize;
    private int totalPage;
    private List<AnnouncementRepDto> announcementList;
}
