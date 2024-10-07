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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.server.youthtalktalk.global.response.BaseResponseCode.*;

@Controller
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;

    /**
     * 공지사항 상세 조회
     */
    @GetMapping("/announcements/{id}")
    @ResponseBody
    public BaseResponse<AnnouncementRepDto> getAnnouncement(@PathVariable Long id) {
        AnnouncementRepDto announcementRepDto = announcementService.getAnnouncement(id);
        return new BaseResponse<>(announcementRepDto, SUCCESS);
    }

    /**
     * 공지사항 전체 조회
     */
    @GetMapping("/announcements")
    @ResponseBody
    public BaseResponse<AnnouncementListRepDto> getAnnouncementList(Pageable pageable) {
        AnnouncementListRepDto announcementListRepDto = announcementService.getAnnouncementList(pageable);
        return new BaseResponse<>(announcementListRepDto, SUCCESS);
    }

    /* 관리자 페이지 */

    /**
     * 공지사항 전체 조회 view
     */
    @GetMapping("/admin/announcements/list")
    public String list(Pageable pageable,Model model) {
        AnnouncementListRepDto announcementListRepDto = announcementService.getAnnouncementList(pageable);
        model.addAttribute("postList", announcementListRepDto.getAnnouncementList());
        model.addAttribute("totalPages",announcementListRepDto.getTotalPage());
        model.addAttribute("currentPage",announcementListRepDto.getPageNum());
        model.addAttribute("start",announcementListRepDto.getPageNum()-5);
        model.addAttribute("end",announcementListRepDto.getPageNum()+5);
        return "admin/announcements/list";
    }
    /**
     * 공지사항 상세 조회 view
     */
    @GetMapping("/admin/announcements/{id}")
    public String detail(@PathVariable Long id,Model model) {
        AnnouncementRepDto announcementRepDto = announcementService.getAnnouncement(id);
        model.addAttribute("announcement",announcementRepDto);
        return "admin/announcements/detail";
    }
    /**
     * 공지사항 등록 view
     */
    @GetMapping("/admin/announcements")
    public String create(Model model) {
        AnnouncementCreateDto announcementCreateDto = new AnnouncementCreateDto("","");
        model.addAttribute("announcementCreateDto", announcementCreateDto);
        return "admin/announcements/write";
    }

    /**
     * 공지사항 등록
     */
    @PostMapping("/admin/announcements")
    @ResponseBody
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
    @ResponseBody
    public BaseResponse<Void> updateAnnouncement(@RequestPart("content") @Valid AnnouncementUpdateDto announcementUpdateDto,
                                                 @RequestPart(value = "images",required = false) List<MultipartFile> fileList,
                                                 @PathVariable Long id) {
        announcementService.updateAnnouncement(id, announcementUpdateDto,fileList);
        return new BaseResponse<>(SUCCESS_ANNOUNCEMENT_UPDATE);
    }

    /**
     * 공지사항 삭제
     */
    @PostMapping("/admin/announcements/{id}")
    public String deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return "redirect:/admin/announcements/list";
    }

    /**
     * 관리자 로그인 view
     */
    @GetMapping("/admin/login")
    public String login(){
        return "admin/login";
    }
}
