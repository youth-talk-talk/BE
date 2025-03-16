package com.server.youthtalktalk.domain.policy.entity.region;

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

    public static Region fromName(String name) {
        for (Region region : Region.values()) {
            if (region.getName().equals(name)) {
                return region;
            }
        }
        return null;
    }

    public static Region fromKey(String key) {
        for (Region region : Region.values()) {
            if (region.getKey().equals(key)) {
                return region;
            }
        }
        return null;
    }
}
