package com.server.youthtalktalk.domain.image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.youthtalktalk.domain.Announcement;
import com.server.youthtalktalk.domain.BaseTimeEntity;
import com.server.youthtalktalk.domain.post.Post;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
public class Image extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "img_id")
    private Long id;

    private String imgUrl;
}
