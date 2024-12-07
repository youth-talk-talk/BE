package com.server.youthtalktalk.domain.announcement.service;

import com.server.youthtalktalk.domain.announcement.entity.Announcement;
import com.server.youthtalktalk.domain.image.entity.AnnouncementImage;
import com.server.youthtalktalk.domain.announcement.dto.AnnouncementCreateDto;
import com.server.youthtalktalk.domain.announcement.dto.AnnouncementListRepDto;
import com.server.youthtalktalk.domain.announcement.dto.AnnouncementListRepDto.AnnouncementDto;
import com.server.youthtalktalk.domain.announcement.dto.AnnouncementRepDto;
import com.server.youthtalktalk.domain.announcement.dto.AnnouncementUpdateDto;
import com.server.youthtalktalk.global.response.exception.announcement.AnnouncementNotFoundException;
import com.server.youthtalktalk.domain.announcement.repository.AnnouncementRepository;
import com.server.youthtalktalk.domain.image.service.ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService{
    private final AnnouncementRepository announcementRepository;
    private final ImageService imageService;

    @Override
    public AnnouncementListRepDto getAnnouncementList(Pageable pageable) {
        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC,"id"));
        Page<Announcement> announcements = announcementRepository.findAll(pageRequest);
        List<AnnouncementDto> announcementDtoList = announcements.stream().map(announcement -> toAnnouncementDto(announcement)).toList();
        return AnnouncementListRepDto.builder()
                .announcementList(announcementDtoList)
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
    public Long createAnnouncement(AnnouncementCreateDto announcementCreateDto, List<MultipartFile> fileList) throws IOException {
        Announcement announcement = Announcement.builder()
                .title(announcementCreateDto.title())
                .content(announcementCreateDto.content())
                .build();
        Announcement savedAnnouncement = announcementRepository.save(announcement);

        if(fileList!=null&&!fileList.isEmpty()) {
            List<String> imageUrlList = imageService.uploadMultiFiles(fileList);
            imageService.saveAnnouncementImageList(imageUrlList, savedAnnouncement);
        }
        return announcement.getId();
    }

    @Override
    public void updateAnnouncement(Long announcementId, AnnouncementUpdateDto announcementUpdateDto, List<MultipartFile> fileList) {
        announcementRepository.findById(announcementId).ifPresentOrElse(
                announcement -> {
                    announcementUpdateDto.title().ifPresent(announcement::updateTitle);
                    announcementUpdateDto.content().ifPresent(announcement::updateContent);
                    Announcement savedAnnouncement = announcementRepository.save(announcement);

                    if(!fileList.isEmpty()) {
                        try {
                            List<String> imageUrlList = imageService.uploadMultiFiles(fileList);
                            imageService.saveAnnouncementImageList(imageUrlList, savedAnnouncement);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    announcementUpdateDto.deletedImgList().ifPresent(imageService::deleteMultiFile);
                }, () -> {
                    throw new AnnouncementNotFoundException();
                }
        );
    }

    @Override
    public void deleteAnnouncement(Long announcementId) {
        announcementRepository.findById(announcementId).ifPresentOrElse(
                announcement -> {
                    announcementRepository.delete(announcement);
                    imageService.deleteMultiFile(announcement.getImages().stream()
                                    .map(AnnouncementImage::getImgUrl)
                                    .toList()
                    );
                },
                () -> {
                    throw new AnnouncementNotFoundException();
                }
        );

    }

    public AnnouncementDto toAnnouncementDto(Announcement announcement) {
        return AnnouncementDto.builder()
                .title(announcement.getTitle())
                .id(announcement.getId())
                .updateAt(announcement.getUpdatedAt().toString())
                .build();
    }
}
