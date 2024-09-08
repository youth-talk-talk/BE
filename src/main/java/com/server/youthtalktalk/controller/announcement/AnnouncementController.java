package com.server.youthtalktalk.controller.announcement;

import com.server.youthtalktalk.dto.announcement.AnnouncementCreateDto;
import com.server.youthtalktalk.dto.announcement.AnnouncementListRepDto;
import com.server.youthtalktalk.dto.announcement.AnnouncementRepDto;
import com.server.youthtalktalk.dto.announcement.AnnouncementUpdateDto;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.service.announcement.AnnouncementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.server.youthtalktalk.global.response.BaseResponseCode.*;

@RestController
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;

    /**
     * 공지사항 상세 조회
     */
    @GetMapping("/announcements/{id}")
    public BaseResponse<AnnouncementRepDto> getAnnouncement(@PathVariable Long id) {
        AnnouncementRepDto announcementRepDto = announcementService.getAnnouncement(id);
        return new BaseResponse<>(announcementRepDto, SUCCESS);
    }

    /**
     * 공지사항 전체 조회
     */
    @GetMapping("/announcements")
    public BaseResponse<AnnouncementListRepDto> getAnnouncementList(Pageable pageable) {
        AnnouncementListRepDto announcementListRepDto = announcementService.getAnnouncementList(pageable);
        return new BaseResponse<>(announcementListRepDto, SUCCESS);
    }

    /**
     * 공지사항 등록
     */
    @PostMapping("/admin/announcements")
    public BaseResponse<Map<String, Long>> createAnnouncement(@RequestPart("content") @Valid AnnouncementCreateDto announcementCreateDto,
                                                              @RequestPart(value = "images",required = false) List<MultipartFile> fileList) throws IOException {
        Long announcementId = announcementService.createAnnouncement(announcementCreateDto,fileList);
        Map<String, Long> data = new HashMap<>();
        data.put("announcementId", announcementId);
        return new BaseResponse<>(data, SUCCESS_ANNOUNCEMENT_CREATE);
    }

    /**
     * 공지사항 수정
     */
    @PatchMapping("/admin/announcements/{id}")
    public BaseResponse<Void> updateAnnouncement(@RequestPart("content") @Valid AnnouncementUpdateDto announcementUpdateDto,
                                                 @RequestPart(value = "images",required = false) List<MultipartFile> fileList,
                                                 @PathVariable Long id) {
        announcementService.updateAnnouncement(id, announcementUpdateDto,fileList);
        return new BaseResponse<>(SUCCESS_ANNOUNCEMENT_UPDATE);
    }

    /**
     * 공지사항 삭제
     */
    @DeleteMapping("/admin/announcements/{id}")
    public BaseResponse<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return new BaseResponse<>(SUCCESS_ANNOUNCEMENT_DELETE);
    }
}
