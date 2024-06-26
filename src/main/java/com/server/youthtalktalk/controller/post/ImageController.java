package com.server.youthtalktalk.controller.post;

import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.service.image.ImageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class ImageController {
    private final ImageServiceImpl imageService;

    @PostMapping("/images")
    public BaseResponse<String> uploadImage(@RequestParam("image") MultipartFile image) throws IOException {
        String imgUrl = imageService.uploadFile(image);
        return new BaseResponse<>(imgUrl, BaseResponseCode.SUCCESS);
    }
}
