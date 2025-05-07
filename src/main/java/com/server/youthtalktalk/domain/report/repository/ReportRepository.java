package com.server.youthtalktalk.domain.report.repository;

import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.report.entity.CommentReport;
import com.server.youthtalktalk.domain.report.entity.Report;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT COUNT(pr) > 0 FROM PostReport pr WHERE pr.post = :post AND pr.reporter = :reporter")
    boolean existsByPostAndReporter(Post post, Member reporter);

    @Query("SELECT COUNT(cr) > 0 FROM CommentReport cr WHERE cr.comment.id = :commentId AND cr.reporter.id = :reporterId")
    boolean existsByComment_IdAndReporter_Id(Long commentId, Long reporterId);

    @Query("SELECT cr FROM CommentReport cr WHERE cr.reporter = :reporter")
    List<CommentReport> findCommentReportsByReporter(@Param("reporter") Member reporter);
}
