package com.server.youthtalktalk.domain.scrap.repository;

import com.server.youthtalktalk.domain.ItemType;
import com.server.youthtalktalk.domain.scrap.dto.PolicyScrapInfoDto;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ScrapRepositoryCustom {
    List<PolicyScrapInfoDto> findRecentByDeadlineOrScrapDate();
}
