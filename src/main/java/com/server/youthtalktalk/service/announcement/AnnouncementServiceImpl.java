package com.server.youthtalktalk.service.announcement;

import com.server.youthtalktalk.domain.Announcement;
import com.server.youthtalktalk.dto.announcement.AnnouncementCreateDto;
import com.server.youthtalktalk.dto.announcement.AnnouncementListRepDto;
import com.server.youthtalktalk.dto.announcement.AnnouncementRepDto;
import com.server.youthtalktalk.global.response.exception.announcement.AnnouncementNotFoundException;
import com.server.youthtalktalk.repository.AnnouncementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService{
    private final AnnouncementRepository announcementRepository;

    @Override
    public AnnouncementListRepDto getAnnouncementList(Pageable pageable) {
        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC,"id"));
        Page<Announcement> announcements = announcementRepository.findAll(pageRequest);
        List<AnnouncementRepDto> announcementRepDtoList = announcements.stream().map(Announcement::toAnnouncementRepDto).toList();
        return AnnouncementListRepDto.builder()
                .announcementList(announcementRepDtoList)
                .totalPage(announcements.getTotalPages())
                .pageSize(announcements.getSize())
                .pageNum(announcements.getNumber())
                .build();
    }

    @Override
    public AnnouncementRepDto getAnnouncement(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId).orElseThrow(AnnouncementNotFoundException::new);
        return announcement.toAnnouncementRepDto();
    }

    @Override
    public Long createAnnouncement(AnnouncementCreateDto announcementCreateDto) {
        Announcement announcement = Announcement.builder()
                .title(announcementCreateDto.title())
                .content(announcementCreateDto.content())
                .build();
        announcementRepository.save(announcement);
        return announcement.getId();
    }
}
