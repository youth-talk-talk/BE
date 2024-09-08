package com.server.youthtalktalk.controller.post;

import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.service.image.ImageService;
import com.server.youthtalktalk.service.image.ImageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/image")
    public BaseResponse<String> uploadImage(@RequestParam("image") MultipartFile image) throws IOException {
        String imgUrl = imageService.uploadMultiFile(image);
        imageService.createPostImage(imgUrl);
        return new BaseResponse<>(imgUrl, BaseResponseCode.SUCCESS);
    }
}
