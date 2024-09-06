package com.server.youthtalktalk.service.announcement;

import com.server.youthtalktalk.dto.announcement.AnnouncementCreateDto;
import com.server.youthtalktalk.dto.announcement.AnnouncementListRepDto;
import com.server.youthtalktalk.dto.announcement.AnnouncementRepDto;
import com.server.youthtalktalk.dto.announcement.AnnouncementUpdateDto;
import org.springframework.data.domain.Pageable;

public interface AnnouncementService {
    AnnouncementListRepDto getAnnouncementList(Pageable pageable);
    AnnouncementRepDto getAnnouncement(Long announcementId);
    Long createAnnouncement(AnnouncementCreateDto announcementCreateDto);
    void updateAnnouncement(Long announcementId, AnnouncementUpdateDto announcementUpdateDto);
}
