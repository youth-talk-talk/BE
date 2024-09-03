package com.server.youthtalktalk.controller.announcement;

import com.server.youthtalktalk.dto.announcement.AnnouncementListRepDto;
import com.server.youthtalktalk.dto.announcement.AnnouncementRepDto;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.service.announcement.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/announcement")
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;

    /** 공지사항 상세 조회 */
    @GetMapping("/{id}")
    public BaseResponse<AnnouncementRepDto> getAnnouncement(@PathVariable Long id) {
        AnnouncementRepDto announcementRepDto = announcementService.getAnnouncement(id);
        return new BaseResponse<>(announcementRepDto, BaseResponseCode.SUCCESS);
    }

    /** 공지사항 전체 조회 */
    @GetMapping("")
    public BaseResponse<AnnouncementListRepDto> getAnnouncementList(Pageable pageable) {
        AnnouncementListRepDto announcementListRepDto = announcementService.getAnnouncementList(pageable);
        return new BaseResponse<>(announcementListRepDto, BaseResponseCode.SUCCESS);
    }
}
