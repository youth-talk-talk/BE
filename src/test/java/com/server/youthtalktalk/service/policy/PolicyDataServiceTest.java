package com.server.youthtalktalk.service.policy;

import com.server.youthtalktalk.domain.policy.dto.data.PolicyData;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.service.data.PolicyDataService;
import com.server.youthtalktalk.domain.policy.service.data.PolicyDataServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class PolicyDataServiceTest {
    @InjectMocks
    private PolicyDataServiceImpl policyDataService;
    @Mock
    private PolicyData policyData;

    @BeforeEach
    void init(){
        policyData = PolicyData.builder()
                .plcyNo("20250313005400210610")
                .bscPlanCycl("1")
                .bscPlanPlcyWayNo("002")
                .bscPlanFcsAsmtNo("006")
                .bscPlanAsmtNo("013")
                .pvsnInstGroupCd("0054002")
                .plcyPvsnMthdCd("0042013")
                .plcyAprvSttsCd("0044002")
                .plcyNm("청년 우대 반값 중개수수료 사업")
                .plcyExplnCn("대학생 및 사회초년생의 경제적 부담 완화와 주거안정지원을 위하여 부동산 전‧월세 계약 시 중개수수료를 감면 서비스 제공")
                .plcySprtCn("1. 대 상 자 : 부산 남구에 거주하거나 거주하려는 18~29세 1인 청년 가구\n2. 사업내용 : 지정된 중개업소에서 전‧월세 보증금 1억원 이하 임대차 계약시 중개수수료 50% 감면\n  * 지정 중개업소 확인 : 남구청 홈페이지(https://www.bsnamgu.go.kr/index.namgu?menuCd=DOM_000000125006020000)\n\n")
                .sprvsnInstCd("3310000")
                .sprvsnInstCdNm("남구")
                .sprvsnInstPicNm("부동산관리팀")
                .operInstCd("       ")
                .operInstCdNm("")
                .operInstPicNm("")
                .sprtSclLmtYn("Y")
                .aplyPrdSeCd("0057002")
                .bizPrdSeCd("0056002")
                .bizPrdBgngYmd("        ")
                .bizPrdEndYmd("        ")
                .bizPrdEtcCn("사업종료 시 별도 공지")
                .plcyAplyMthdCn("")
                .srngMthdCn("")
                .aplyUrlAddr("")
                .sbmsnDcmntCn("")
                .etcMttrCn("")
                .refUrlAddr1("https://www.bsnamgu.go.kr/index.namgu?menuCd=DOM_000000125006020000")
                .refUrlAddr2("")
                .sprtSclCnt("0")
                .sprtArvlSeqYn("N")
                .sprtTrgtMinAge("18")
                .sprtTrgtMaxAge("29")
                .sprtTrgtAgeLmtYn("N")
                .mrgSttsCd("0055003")
                .earnCndSeCd("0043001")
                .earnMinAmt("0")
                .earnMaxAmt("0")
                .earnEtcCn("")
                .addAplyQlfcCndCn("지정된 중개업소에서 전‧월세 보증금 1억원 이하 임대차 계약시 중개수수료 50% 감면")
                .ptcpPrpTrgtCn("")
                .inqCnt("52")
                .rgtrInstCd("3310000")
                .rgtrInstCdNm("부산광역시 남구")
                .rgtrUpInstCd("6260000")
                .rgtrUpInstCdNm("부산광역시")
                .rgtrHghrkInstCd("6260000")
                .rgtrHghrkInstCdNm("부산광역시")
                .zipCd("26290")
                .plcyMajorCd("0011009")
                .jobCd("0013010")
                .schoolCd("0049010")
                .aplyYmd("")
                .frstRegDt("2025-03-13 17:53:59")
                .lastMdfcnDt("2025-03-14 09:34:49")
                .sbizCd("0014010")
                .build();
    }

    @DisplayName("신청 기간 파싱 성공")
    @Test
    void successParsingApplyDate(){
        String applyDate = "20250401 ~ 20250430\\N20250801 ~ 20250829";
        String[] dates = applyDate.split("\\\\N");
        assertThat(dates[0]).isEqualTo("20250401 ~ 20250430");
    }

    @DisplayName("예외 감지로 인한 정책 데이터 엔티티 변환 실패, 다른 데이터 이어서 변환 성공")
    @Test
    void successGetPolicyEntityListForOtherDataIfCatchException(){
        // Given
        PolicyData policyData2 = PolicyData.builder().build(); // 잘못된 데이터
        List<PolicyData> policyDataList = Arrays.asList(policyData, policyData2);
        // When
        List<Policy> policyList = policyDataService.getPolicyEntityList(policyDataList).block();
        // Then
        assertThat(policyList).hasSize(1);
        assertThat(policyList.get(0).getPolicyId()).isEqualTo(policyData.plcyNo());
    }
}
