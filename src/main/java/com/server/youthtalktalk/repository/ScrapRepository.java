package com.server.youthtalktalk.repository;

import com.server.youthtalktalk.domain.Scrap;
import com.server.youthtalktalk.domain.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    // 특정 사용자가 특정 타입의 아이템을 스크랩했는지의 여부
    boolean existsByMemberIdAndItemIdAndItemType(Long memberId, String itemId, ItemType itemType);


    List<Scrap> findAllByItemIdAndItemType(Long itemId, ItemType itemType);

}
