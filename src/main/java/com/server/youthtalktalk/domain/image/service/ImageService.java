package com.server.youthtalktalk.domain.image.service;

import com.server.youthtalktalk.domain.announcement.entity.Announcement;
import com.server.youthtalktalk.domain.post.entity.Post;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    String uploadMultiFile(MultipartFile multipartFile) throws IOException;
    List<String> uploadMultiFiles(List<MultipartFile> multipartFileList) throws IOException;
    void mappingPostImage(List<String> imgUrls,Post post);
    void createPostImage(String imgUrl);
    void deleteMultiFile(List<String> imgUrlList);
    void savePostImageList(List<String> imageUrlList, Post post);
    void saveAnnouncementImageList(List<String> imageUrlList, Announcement announcement);
}
