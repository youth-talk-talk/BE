package com.server.youthtalktalk.domain.report.service;

import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.post.repostiory.PostRepository;
import com.server.youthtalktalk.domain.report.entity.PostReport;
import com.server.youthtalktalk.domain.report.entity.Report;
import com.server.youthtalktalk.domain.report.repository.ReportRepository;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;
import com.server.youthtalktalk.global.response.exception.post.PostNotFoundException;
import com.server.youthtalktalk.global.response.exception.report.ReportAlreadyExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;

    @Override
    public Report reportPost(Long postId, Member reporter) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        if(reportRepository.existsByPostAndReporter(post, reporter)){
            throw new ReportAlreadyExistException();
        }
        else{
            return reportRepository.save(PostReport.builder()
                            .reporter(reporter)
                            .post(post)
                            .build());
        }
    }
}
