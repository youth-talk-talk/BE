package com.server.youthtalktalk.domain.image.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.youthtalktalk.domain.announcement.entity.Announcement;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("announcement")
public class AnnouncementImage extends Image{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id")
    @JsonIgnore
    private Announcement announcement;

    public void setAnnouncement(Announcement announcement){
        this.announcement = announcement;
        if(announcement != null){
            this.announcement.getImages().add(this);
        }
    }
}
