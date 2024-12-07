package com.server.youthtalktalk.domain.report.repository;

import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT COUNT(pr) > 0 FROM PostReport pr WHERE pr.post = :post AND pr.reporter = :reporter")
    boolean existsByPostAndReporter(Post post, Member reporter);
}
