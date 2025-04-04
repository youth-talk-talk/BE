package com.server.youthtalktalk.domain.scrap.repository;

import com.server.youthtalktalk.domain.scrap.entity.Scrap;
import com.server.youthtalktalk.domain.ItemType;
import com.server.youthtalktalk.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    // 특정 사용자가 특정 타입의 아이템을 스크랩했는지의 여부
    boolean existsByMemberIdAndItemIdAndItemType(Long memberId, Long itemId, ItemType itemType);
    Optional<Scrap> findByMemberAndItemIdAndItemType(Member memberId, Long itemId, ItemType itemType);
    List<Scrap> findAllByItemIdAndItemType(Long itemId, ItemType itemType);
    void deleteAllByItemIdAndItemType(Long itemId, ItemType itemType);
}
