package com.server.youthtalktalk.domain.scrap.entity;

import com.server.youthtalktalk.domain.ItemType;
import com.server.youthtalktalk.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrap_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    private String itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder(toBuilder = true)
    public Scrap(ItemType itemType, String itemId, Member member) {
        this.itemType = itemType;
        this.itemId = itemId;
        this.member = member;
    }
}