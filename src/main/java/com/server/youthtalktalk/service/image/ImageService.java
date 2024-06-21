package com.server.youthtalktalk.service.image;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    String uploadFile(MultipartFile file) throws IOException;
    void deleteFile(String fileUrl);
}
