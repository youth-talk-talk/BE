package com.server.youthtalktalk.domain.policy.entity.region;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class Province {
//    @Getter
//    @RequiredArgsConstructor
//    public enum Gyeonggi{
//        GapyeongGun("41820", "가평군"),
//        Goyang_DeokyangGu("41281", "고양시 덕양구"),
//        Goyang_IlSanDongGu("41285", "고양시 일산동구"),
//        Goyang_IlSanSeoGu("41287", "고양시 일산서구"),
//        Gwacheon("41290", "과천시"),
//        Gwangmyeong("41210", "광명시"),
//        Gwangju("41610", "광주시"),
//        Guri("41310", "구리시"),
//        Gunpo("41410", "군포시"),
//        Gimpo("41570", "김포시"),
//        NamYangJu("41360", "남양주시"),
//        Dongducheon("41250", "동두천시"),
//        Bucheon_SosaGu("41194", "부천시 소사구"),
//        Bucheon_OjeongGu("41196", "부천시 오정구"),
//        Bucheon_WonmiGu("41192", "부천시 원미구"),
//        SeongNam_BundangGu("41135", "성남시 분당구"),
//        SeongNam_SujeongGu("41131", "성남시 수정구"),
//        SeongNam_JungwonGu("41133", "성남시 중원구"),
//        Suwon_GwonSeonGu("41113", "수원시 권선구"),
//        Suwon_YeongTongGu("41117", "수원시 영통구"),
//        Suwon_JangAnGu("41111", "수원시 장안구"),
//        Suwon_PalDalGu("41115", "수원시 팔달구"),
//        heung("41390", "시흥시"),
//        AnSan_DanWonGu("41273", "안산시 단원구"),
//        AnSan_SangRokGu("41271", "안산시 상록구"),
//        AnSeong("41550", "안성시"),
//        AnYang_DongAnGu("41173", "안양시 동안구"),
//        AnYang_ManAnGu("41171", "안양시 만안구"),
//        YangJu("41630", "양주시"),
//        YangPyeongGun("41830", "양평군"),
//        YeoJu("41670", "여주시"),
//        YeonCheonGun("41800", "연천군"),
//        OSan("41370", "오산시"),
//        YongIn_GiheungGu("41463", "용인시 기흥구"),
//        YongIn_SujiGu("41465", "용인시 수지구"),
//        YongIn_CheoinGu("41461", "용인시 처인구"),
//        EuiWang("41430", "의왕시"),
//        EuiJeongBu("41150", "의정부시"),
//        Icheon("41500", "이천시"),
//        Paju("41480", "파주시"),
//        PyeongTak("41220", "평택시"),
//        Pocheon("41650", "포천시"),
//        HanAm("41450", "하남시"),
//        HwaSeong("41590", "화성시");
//
//        private final String key;
//        private final String name;
//    }
//
//    @Getter
//    @RequiredArgsConstructor
//    public enum Chungbuk{
//        GoesanGun("43760", "괴산군"),
//        DanyangGun("43800", "단양군"),
//        BoeunGun("43720", "보은군"),
//        YeongdongGun("43740", "영동군"),
//        OkcheonGun("43730", "옥천군"),
//        EumseongGun("43770", "음성군"),
//        Jecheon("43150", "제천시"),
//        JeungpyeongGun("43745", "증평군"),
//        JincheonGun("43750", "진천군"),
//        Cheongju_SangdangGu("43111", "청주시 상당구"),
//        Cheongju_SeowonGu("43112", "청주시 서원구"),
//        Cheongju_CheongwonGu("43114", "청주시 청원구"),
//        Cheongju_HeungdeokGu("43113", "청주시 흥덕구"),
//        Chungju("43130", "충주시");
//
//        private final String key;
//        private final String name;
//    }
//
//    @Getter
//    @RequiredArgsConstructor
//    public enum ChungNam{
//        Gyeryong("44250", "계룡시"),
//        Gongju("44150", "공주시"),
//        GeumsanGun("44710", "금산군"),
//        Nonsan("44230", "논산시"),
//        Dangjin("44270", "당진시"),
//        Boryeong("44180", "보령시"),
//        BuyeoGun("44760", "부여군"),
//        Seosan("44210", "서산시"),
//        SeocheonGun("44770", "서천군"),
//        Asan("44200", "아산시"),
//        YesanGun("44810", "예산군"),
//        Cheonan_DongnamGu("44131", "천안시 동남구"),
//        Cheonan_SeobukGu("44133", "천안시 서북구"),
//        CheongyangGun("44790", "청양군"),
//        TaeanGun("44825", "태안군"),
//        HongseongGun("44800", "홍성군");
//
//        private final String key;
//        private final String name;
//    }
//
//    @Getter
//    @RequiredArgsConstructor
//    public enum Jeonnam{
//        GangjinGun("46810", "강진군"),
//        GoehungGun("46770", "고흥군"),
//        GokseongGun("46720", "곡성군"),
//        Gwangyang("46230", "광양시"),
//        GuryeGun("46730", "구례군"),
//        Naju("46170", "나주시"),
//        DamyangGun("46710", "담양군"),
//        Mokpo("46110", "목포시"),
//        MuanGun("46840", "무안군"),
//        BoseongGun("46780", "보성군"),
//        Suncheon("46150", "순천시"),
//        nanGun("46910", "신안군"),
//        Yeosu("46130", "여수시"),
//        YeonggwangGun("46870", "영광군"),
//        YeongamGun("46830", "영암군"),
//        WandoGun("46890", "완도군"),
//        JangseongGun("46880", "장성군"),
//        JangheungGun("46800", "장흥군"),
//        JindoGun("46900", "진도군"),
//        HampyeongGun("46860", "함평군"),
//        HaenamGun("46820", "해남군"),
//        HwasunGun("46790", "화순군");
//
//        private final String key;
//        private final String name;
//    }
//
//    @Getter
//    @RequiredArgsConstructor
//    public enum Gyeongbuk{
//        Gyeongsan("47290", "경산시"),
//        Gyeongju("47130", "경주시"),
//        GoryeongGun("47830", "고령군"),
//        Gumi("47190", "구미시"),
//        Gimcheon("47150", "김천시"),
//        Mungyeong("47280", "문경시"),
//        BonghwaGun("47920", "봉화군"),
//        Sangju("47250", "상주시"),
//        SeongjuGun("47840", "성주군"),
//        Andong("47170", "안동시"),
//        YeongdeokGun("47770", "영덕군"),
//        YeongyangGun("47760", "영양군"),
//        Yeongju("47210", "영주시"),
//        Yeongcheon("47230", "영천시"),
//        YecheonGun("47900", "예천군"),
//        UlleungGun("47940", "울릉군"),
//        UljinGun("47930", "울진군"),
//        UiseongGun("47730", "의성군"),
//        CheongdoGun("47820", "청도군"),
//        CheongsongGun("47750", "청송군"),
//        ChilgokGun("47850", "칠곡군"),
//        PohangNamGu("47111", "포항시 남구"),
//        PohangBukGu("47113", "포항시 북구");
//
//        private final String key;
//        private final String name;
//    }
//
//    @Getter
//    @RequiredArgsConstructor
//    public enum GyeongNam{
//        Geoje("48310", "거제시"),
//        GeochaengGun("48880", "거창군"),
//        GoseongGun("48820", "고성군"),
//        Gimhae("48250", "김해시"),
//        NamhaeGun("48840", "남해군"),
//        Miryang("48270", "밀양시"),
//        Sacheon("48240", "사천시"),
//        SancheongGun("48860", "산청군"),
//        Yangsan("48330", "양산시"),
//        UiryeongGun("48720", "의령군"),
//        Jinju("48170", "진주시"),
//        ChangnyeongGun("48740", "창녕군"),
//        ChangwonMasanhappoGu("48125", "창원시 마산합포구"),
//        ChangwonMasanhweonGu("48127", "창원시 마산회원구"),
//        ChangwonSeongsanGu("48123", "창원시 성산구"),
//        ChangwonUichangGu("48121", "창원시 의창구"),
//        ChangwonJinhaeGu("48129", "창원시 진해구"),
//        Tongyeong("48220", "통영시"),
//        HadongGun("48850", "하동군"),
//        HamanGun("48730", "함안군"),
//        HamyangGun("48870", "함양군"),
//        HapcheonGun("48890", "합천군");
//
//        private final String key;
//        private final String name;
//    }
}
