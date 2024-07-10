package com.server.youthtalktalk.controller.post;

import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.BaseResponseCode;
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
    private final ImageServiceImpl imageService;

    @PostMapping("/images")
    public BaseResponse<List<String>> uploadImage(@RequestParam("images") List<MultipartFile> images) throws IOException {
        List<String> imgUrl = imageService.uploadMultiFile(images);
        return new BaseResponse<>(imgUrl, BaseResponseCode.SUCCESS);
    }
}
