package com.server.youthtalktalk.service.report;

import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.entity.Role;
import com.server.youthtalktalk.domain.member.repository.MemberRepository;
import com.server.youthtalktalk.domain.policy.entity.Region;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.post.repostiory.PostRepository;
import com.server.youthtalktalk.domain.report.entity.PostReport;
import com.server.youthtalktalk.domain.report.entity.Report;
import com.server.youthtalktalk.domain.report.repository.ReportRepository;
import com.server.youthtalktalk.domain.report.service.ReportService;
import com.server.youthtalktalk.global.response.exception.report.ReportAlreadyExistException;
import com.server.youthtalktalk.global.response.exception.report.SelfReportNotAllowedException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ReportTest {
    @Autowired
    private ReportService reportService;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;

    private Member reporter;
    private Member writer;
    private Post post;

    @BeforeEach
    void init(){
        this.reporter = memberRepository.save(Member.builder()
                .nickname("reporter")
                .role(Role.USER)
                .username("reporter")
                .build());
        this.writer = memberRepository.save(Member.builder()
                .nickname("writer")
                .role(Role.USER)
                .username("writer")
                .build());
        this.post = postRepository.save(Post.builder()
                .title("test")
                .writer(writer)
                .build());
    }

    @Test
    @DisplayName("게시글 신고 성공")
    void successReportPost(){
        // when
        Report report = reportService.reportPost(post.getId(), reporter);
        // Then
        assertThat(report.getReporter().getId()).isEqualTo(reporter.getId());
        assertThat(reportRepository.existsByPostAndReporter(post, reporter)).isTrue();
    }

    @Test
    @DisplayName("자신이 작성한 글 신고 실패")
    void failReportPostIfMyPost(){
        Post post = postRepository.save(Post.builder()
                .title("test")
                .writer(reporter)
                .build());

        reportRepository.save(PostReport.builder()
                .reporter(reporter)
                .post(post)
                .build());
        // When, Then
        assertThatThrownBy(() -> reportService.reportPost(post.getId(), reporter))
                .isInstanceOf(SelfReportNotAllowedException.class);
    }

    @Test
    @DisplayName("이미 존재하는 게시글 신고 실패")
    void failReportPostIfAlreadyExist(){
        reportRepository.save(PostReport.builder()
                        .reporter(reporter)
                        .post(post)
                        .build());
        // When, Then
        assertThatThrownBy(() -> reportService.reportPost(post.getId(), reporter))
                .isInstanceOf(ReportAlreadyExistException.class);
    }
}
