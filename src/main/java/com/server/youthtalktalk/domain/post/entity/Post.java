package com.server.youthtalktalk.domain.post.entity;

import com.server.youthtalktalk.domain.BaseTimeEntity;
import com.server.youthtalktalk.domain.image.entity.PostImage;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.comment.entity.PostComment;
import com.server.youthtalktalk.domain.post.dto.PostRepDto;
import com.server.youthtalktalk.domain.report.entity.PostReport;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder(toBuilder = true) // 상속관계에서는 @SuperBuilder를 사용해야하는데 이 부분에서 문제가 생김
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "post_type")
public class Post{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name =  "post_id")
    private Long id;

    @Size(max = 50, message = "Title must be 50 characters or less")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Long view;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ElementCollection
    @CollectionTable(name = "post_contents", joinColumns = @JoinColumn(name = "post_id"))
    private List<Content> contents;

    @Builder.Default
    @OneToMany(mappedBy = "post")
    private List<PostImage> images = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostComment> postComments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostReport> postReports = new ArrayList<>();

    /* 연관관계 메서드 */
    public void setWriter(Member member) {
        this.writer = member;
        if (member != null) {
            member.getPosts().add(this);
        }
    }

    public void setUpdatedAt(){
        this.updatedAt = LocalDateTime.now();
    }

    public PostRepDto toPostRepDto(boolean isScrap) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String profileImage = null;
        if(this.getWriter() != null && this.getWriter().getProfileImage() != null){
            profileImage = this.getWriter().getProfileImage().getImgUrl();
        }
        return PostRepDto.builder()
                .postId(this.getId())
                .title(this.getTitle())
                .contentList(this.getContents())
                .policyId(this instanceof Review ? ((Review)this).getPolicy().getPolicyId() : null)
                .policyTitle(this instanceof Review ? ((Review)this).getPolicy().getTitle() : null)
                .postType(this instanceof Review ? "review" : "post")
                .writerId(this.getWriter() == null ? null : this.getWriter().getId())
                .nickname(this.getWriter() == null ? "null" : this.getWriter().getNickname())
                .view(this.getView())
                .category(this instanceof Review ? ((Review)this).getPolicy().getCategory().name() : null)
                .isScrap(isScrap)
                .updatedAt(this.getUpdatedAt() == null ? null : this.getUpdatedAt().format(formatter))
                .profileImage(profileImage)
                .build();
    }
}
