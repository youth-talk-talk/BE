package com.server.youthtalktalk.service.image;

import com.server.youthtalktalk.domain.Announcement;
import com.server.youthtalktalk.domain.image.Image;
import com.server.youthtalktalk.domain.post.Post;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    List<String> uploadMultiFile(List<MultipartFile> multipartFileList) throws IOException;
    void deleteMultiFile(List<String> fileUrlList);
    void savePostImageList(List<String> imageUrlList, Post post);
    void saveAnnouncementImageList(List<String> imageUrlList, Announcement announcement);
}
