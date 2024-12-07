package com.server.youthtalktalk.domain.post.entity;

import com.server.youthtalktalk.domain.BaseTimeEntity;
import com.server.youthtalktalk.domain.image.entity.PostImage;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.comment.entity.PostComment;
import com.server.youthtalktalk.domain.post.dto.PostRepDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder(toBuilder = true) // 상속관계에서는 @SuperBuilder를 사용해야하는데 이 부분에서 문제가 생김
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "post_type")
public class Post extends BaseTimeEntity {

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

    // 임시 추가
    @ElementCollection
    @CollectionTable(name = "post_contents", joinColumns = @JoinColumn(name = "post_id"))
    private List<Content> contents;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostImage> images = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostComment> postComments = new ArrayList<>();

    /* 연관관계 메서드 */
    public void setWriter(Member member) {
        this.writer = member;
        if (member != null) {
            member.getPosts().add(this);
        }
    }

    public PostRepDto toPostRepDto(boolean isScrap) {
        return PostRepDto.builder()
                .postId(this.getId())
                .title(this.getTitle())
                .content(this.getContent())
                .contentList(this.getContents())
                .policyId(this instanceof Review ? ((Review)this).getPolicy().getPolicyId() : null)
                .policyTitle(this instanceof Review ? ((Review)this).getPolicy().getTitle() : null)
                .postType(this instanceof Review ? "review" : "post")
                .writerId(this.getWriter() == null ? null : this.getWriter().getId())
                .nickname(this.getWriter() == null ? "null" : this.getWriter().getNickname())
                .view(this.getView())
                .images(this.getImages().stream().map(PostImage::getImgUrl).toList())
                .category(this instanceof Review ? ((Review)this).getPolicy().getCategory().getKey() : null)
                .isScrap(isScrap)
                .build();
    }
}
