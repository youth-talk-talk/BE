package com.server.youthtalktalk.domain.image.entity;

import com.server.youthtalktalk.domain.member.entity.Member;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("profile")
public class ProfileImage extends Image{

    @OneToOne(mappedBy = "profileImage")
    private Member member;

    public void setMember(Member member) {
        this.member = member;
        if (member != null) {
            member.updateProfileImage(this);
        }
    }

}
