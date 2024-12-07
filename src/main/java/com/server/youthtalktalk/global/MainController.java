package com.server.youthtalktalk.global;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/")
    public String showVersion() {
        return "2024-12-06 test!"; // 무중단 배포 확인용
    }
}