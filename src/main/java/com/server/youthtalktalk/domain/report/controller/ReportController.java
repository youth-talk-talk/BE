package com.server.youthtalktalk.domain.report.controller;

import com.server.youthtalktalk.domain.member.service.MemberService;
import com.server.youthtalktalk.domain.report.service.ReportService;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final MemberService memberService;

    @PostMapping("/post/{id}")
    public BaseResponse<String> reportPost(@PathVariable Long id){
        reportService.reportPost(id, memberService.getCurrentMember());
        return new BaseResponse<>(BaseResponseCode.SUCCESS);
    }
}
