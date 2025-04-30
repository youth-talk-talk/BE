package com.server.youthtalktalk.domain.report.controller;

import com.server.youthtalktalk.domain.comment.entity.Comment;
import com.server.youthtalktalk.domain.comment.repository.CommentRepository;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.service.MemberService;
import com.server.youthtalktalk.domain.report.repository.ReportRepository;
import com.server.youthtalktalk.domain.report.service.ReportService;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.comment.CommentNotFoundException;
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
    private final CommentRepository commentRepository;

    @PostMapping("/post/{id}")
    public BaseResponse<String> reportPost(@PathVariable Long id){
        reportService.reportPost(id, memberService.getCurrentMember());
        return new BaseResponse<>(BaseResponseCode.SUCCESS);
    }

    @PostMapping("/comments/{id}")
    public BaseResponse<String> reportComment(@PathVariable Long id){
        Member reporter = memberService.getCurrentMember();
        Comment comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);
        reportService.reportComment(comment, reporter);
        return new BaseResponse<>(BaseResponseCode.SUCCESS);
    }

}
