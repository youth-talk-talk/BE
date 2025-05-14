package com.server.youthtalktalk.domain.report.service;

import com.server.youthtalktalk.domain.comment.entity.Comment;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.post.repostiory.PostRepository;
import com.server.youthtalktalk.domain.report.entity.CommentReport;
import com.server.youthtalktalk.domain.report.entity.PostReport;
import com.server.youthtalktalk.domain.report.entity.Report;
import com.server.youthtalktalk.domain.report.repository.ReportRepository;
import com.server.youthtalktalk.global.response.exception.post.PostNotFoundException;
import com.server.youthtalktalk.global.response.exception.report.ReportAlreadyExistException;
import com.server.youthtalktalk.global.response.exception.report.SelfReportNotAllowedException;
import lombok.RequiredArgsConstructor;
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
        if(reporter.getId() == post.getWriter().getId()) { // 자신이 작성한 글을 신고할 경우
            throw new SelfReportNotAllowedException();
        }
        if(reportRepository.existsByPostAndReporter(post, reporter)){
            throw new ReportAlreadyExistException();
        }

        return reportRepository.save(PostReport.builder()
                        .reporter(reporter)
                        .post(post)
                        .build());
    }

    @Override
    public Report reportComment(Comment comment, Member reporter) {
        // 자신이 작성한 댓글을 신고할 경우
        if (comment.getWriter().getId().equals(reporter.getId())) {
            throw new SelfReportNotAllowedException();
        }
        // 이미 신고한 댓글인 경우
        if (reportRepository.existsByComment_IdAndReporter_Id(comment.getId(), reporter.getId())) {
            throw new ReportAlreadyExistException();
        }
        return reportRepository.save(CommentReport.builder()
                .reporter(reporter)
                .comment(comment)
                .build());
    }
}
