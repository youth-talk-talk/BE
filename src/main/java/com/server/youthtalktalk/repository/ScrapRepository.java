package com.server.youthtalktalk.repository;

import com.server.youthtalktalk.domain.ItemType;
import com.server.youthtalktalk.domain.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    List<Scrap> findAllByItemIdAndItemType(Long itemId, ItemType itemType);
}
