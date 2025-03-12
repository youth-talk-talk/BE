package com.server.youthtalktalk.domain.policy.entity.region;

import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.InvalidValueException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Region {
    SEOUL("6110000","서울"),
    BUSAN("6260000","부산"),
    DAEGU("6270000","대구"),
    INCHEON("6280000","인천"),
    GWANGJU("6290000","광주"),
    DAEJEON("6300000","대전"),
    ULSAN("6310000","울산"),
    GYEONGGI("6410000","경기"),
    GANGWON("6420000","강원"),
    CHUNGBUK("6430000","충북"),
    CHUNGNAM("6440000","충남"),
    JEONBUK("6540000","전북"),
    JEONNAM("6460000","전남"),
    GYEONGBUK("6470000","경북"),
    GYEONGNAM("6480000","경남"),
    JEJU("6500000","제주"),
    SEJONG("5690000","세종"),
    ALL("중앙부처","전국");

    private final String key;
    private final String name;
    private static final Set<String> REGION_NAME_SET = new HashSet<>();
    static {
        for (Region region : values()) {
            REGION_NAME_SET.add(region.name);
        }
    }

    // TODO 아래 fromName 메서드와 같은 기능 - 리팩토링 예정
    public static Region fromRegionStr(String regionStr) {
        for (Region region : Region.values()) {
            if (region.getName().equalsIgnoreCase(regionStr)) {
                return region;
            }
        }
        throw new InvalidValueException(BaseResponseCode.INVALID_INPUT_VALUE); // 맞는 Region이 없는 경우
    }

    public static Region fromKey(String key) {
        for (Region region : Region.values()) {
            if (region.getKey().equals(key)) {
                return region;
            }
        }
        return null; // 맞는 Region이 없는 경우
    }

    public static Region fromName(String name) {
        if (REGION_NAME_SET.contains(name)) {
            return Region.valueOf(name);
        }
        return null;
    }
}
