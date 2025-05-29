package com.server.youthtalktalk.domain.report.entity;

import com.server.youthtalktalk.domain.comment.entity.Comment;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@DiscriminatorValue("comment")
public class CommentReport extends Report {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    /* 연관관계 메서드 */
    public void setComment(Comment comment) {
        this.comment = comment;
        if (comment != null) {
            comment.getCommentReports().add(this);
        }
    }
}
