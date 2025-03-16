package com.server.youthtalktalk.domain.policy.entity.region;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.InvalidValueException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Region {
    SEOUL("6110000","서울", 11),
    BUSAN("6260000","부산", 26),
    DAEGU("6270000","대구", 27),
    INCHEON("6280000","인천", 28),
    GWANGJU("6290000","광주", 29),
    DAEJEON("6300000","대전", 30),
    ULSAN("6310000","울산", 31),
    GYEONGGI("6410000","경기", 41),
    GANGWON("6530000","강원", 51),
    CHUNGBUK("6430000","충북", 43),
    CHUNGNAM("6440000","충남", 44),
    JEONBUK("6540000","전북", 52),
    JEONNAM("6460000","전남", 46),
    GYEONGBUK("6470000","경북", 47),
    GYEONGNAM("6480000","경남", 48),
    JEJU("6500000","제주", 50),
    SEJONG("5690000","세종", 36),
    ALL("중앙부처","전국", -1);

    private final String key;
    private final String name;
    private final int num;

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

    public static Region fromNum(int num) {
        for (Region region : Region.values()) {
            if(region.getNum() == num){
                return region;
            }
        }
        return null;
    }
}
