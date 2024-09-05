package com.server.youthtalktalk.domain;

import com.server.youthtalktalk.domain.image.AnnouncementImage;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.dto.announcement.AnnouncementRepDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Announcement extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "announcement_id")
    private Long id;

    @Size(max = 50, message = "Title must be 50 characters or less")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @OneToMany(mappedBy = "announcement", cascade = CascadeType.ALL)
    private List<AnnouncementImage> images = new ArrayList<>();

    public AnnouncementRepDto toAnnouncementRepDto() {
        return AnnouncementRepDto.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .updateAt(this.getUpdatedAt().toString())
                .imageList(this.getImages())
                .build();
    }
}
