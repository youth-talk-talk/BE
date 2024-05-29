package com.server.youthtalktalk.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/")
    public String showVersion() {
        return "TEST"; // 무중단 배포 확인용
    }
}