package com.server.youthtalktalk.domain.report.service;

import com.server.youthtalktalk.domain.comment.entity.Comment;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.report.entity.Report;

public interface ReportService {
    Report reportPost(Long postId, Member reporter);
    Report reportComment(Comment comment, Member reporter);
}
