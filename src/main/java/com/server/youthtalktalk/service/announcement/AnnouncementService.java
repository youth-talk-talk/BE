package com.server.youthtalktalk.service.announcement;

import com.server.youthtalktalk.dto.announcement.AnnouncementCreateDto;
import com.server.youthtalktalk.dto.announcement.AnnouncementListRepDto;
import com.server.youthtalktalk.dto.announcement.AnnouncementRepDto;
import com.server.youthtalktalk.dto.announcement.AnnouncementUpdateDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AnnouncementService {
    AnnouncementListRepDto getAnnouncementList(Pageable pageable);
    AnnouncementRepDto getAnnouncement(Long announcementId);
    Long createAnnouncement(AnnouncementCreateDto announcementCreateDto, List<MultipartFile> fileList) throws IOException;
    void updateAnnouncement(Long announcementId, AnnouncementUpdateDto announcementUpdateDto, List<MultipartFile> fileList);
    void deleteAnnouncement(Long announcementId);
}
