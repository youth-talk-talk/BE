package com.server.youthtalktalk.domain.policy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Region {
    SEOUL("003002001","서울"),
    BUSAN("003002002","부산"),
    DAEGU("003002003","대구"),
    INCHEON("003002004","인천"),
    GWANGJU("003002005","광주"),
    DAEJEON("003002006","대전"),
    ULSAN("003002007","울산"),
    GYEONGGI("003002008","경기"),
    GANGWON("003002009","강원"),
    CHUNGBUK("003002010","충북"),
    CHUNGNAM("003002011","충남"),
    JEONBUK("003002012","전북"),
    JEONNAM("003002013","전남"),
    GYEONGBUK("003002014","경북"),
    GYEONGNAM("003002015","경남"),
    JEJU("003002016","제주"),
    SEJONG("003002017","세종");

    private final String key;
    private final String name;
}
