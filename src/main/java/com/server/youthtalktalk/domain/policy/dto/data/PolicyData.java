package com.server.youthtalktalk.domain.policy.dto.data;


import com.server.youthtalktalk.domain.policy.entity.*;
import com.server.youthtalktalk.domain.policy.entity.condition.*;
import com.server.youthtalktalk.domain.policy.entity.region.Region;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Builder(toBuilder = true)
public record PolicyData(
        String operInstCdNm,
        String bscPlanAsmtNo,
        String sprvsnInstCd,
        String sprvsnInstCdNm,
        String sprvsnInstPicNm,
        String operInstCd,
        String srngMthdCn,
        String operInstPicNm,
        String sprtSclLmtYn,
        String aplyPrdSeCd,
        String bizPrdSeCd,
        String bizPrdBgngYmd,
        String bizPrdEndYmd,
        String bizPrdEtcCn,
        String plcyAplyMthdCn,
        String aplyUrlAddr,
        String sbmsnDcmntCn,
        String etcMttrCn,
        String refUrlAddr1,
        String refUrlAddr2,
        String inqCnt,
        String zipCd,
        String plcyMajorCd,
        String jobCd,
        String sbizCd,
        String schoolCd,
        String bscPlanCycl,
        String sprtSclCnt,
        String sprtArvlSeqYn,
        String sprtTrgtMinAge,
        String sprtTrgtMaxAge,
        String sprtTrgtAgeLmtYn,
        String mrgSttsCd,
        String earnCndSeCd,
        String earnMinAmt,
        String earnMaxAmt,
        String earnEtcCn,
        String addAplyQlfcCndCn,
        String rgtrUpInstCd,
        String rgtrUpInstCdNm,
        String frstRegDt,
        String lastMdfcnDt,
        String bscPlanPlcyWayNo,
        String bscPlanFcsAsmtNo,
        String ptcpPrpTrgtCn,
        String pvsnInstGroupCd,
        String plcyPvsnMthdCd,
        String plcyAprvSttsCd,
        String plcyNm,
        String plcyExplnCn,
        String plcySprtCn,
        String plcyNo,
        String aplyYmd,
        String rgtrInstCd,
        String rgtrInstCdNm,
        String rgtrHghrkInstCd,
        String rgtrHghrkInstCdNm
) {
    private static final String regionCode = "0054002";
    private static final int DEFAULT_MIN_AGE = 0;
    private static final int DEFAULT_MAX_AGE = 100;
    private static final int DEFAULT_MIN_EARN = 0;
    private static final int DEFAULT_MAX_EARN = 0;
    private static final Set<String> INVALID_STRINGS = Set.of("null", "NULL", "-");

    public Policy toPolicy(Department department) {
        RepeatCode repeatCode = RepeatCode.fromKey(plcyNo, aplyPrdSeCd);

        try {
            String[] dates = aplyYmd.split("\\\\N");
            if (dates.length > 1 && !isEqualApplyDates(dates)) { // 서로 다른 신청 기간이 여러 개인 경우
                repeatCode = RepeatCode.ALWAYS; // 상시 처리
            }

            LocalDate applyStart = null;
            LocalDate applyDue = null;

            // 반복코드가 상시인 경우 신청기간 null 처리
            if(!repeatCode.equals(RepeatCode.ALWAYS)){
                LocalDate[] applyDates = parsingApplyYmd(dates[0]);
                applyStart = applyDates[0];
                applyDue = applyDates[1];
            }

            Earn earn = Earn.fromKey(plcyNo, earnCndSeCd);
            Boolean isLimitedAge = sprtTrgtAgeLmtYn == null ? null : "N".equals(sprtTrgtAgeLmtYn);
            Region region = findRegion();
            LocalDate[] bizTerm = parsingBizTerm();

            int minAge = parseAge(isLimitedAge, sprtTrgtMinAge, DEFAULT_MIN_AGE);
            int maxAge = parseAge(isLimitedAge, sprtTrgtMaxAge, DEFAULT_MAX_AGE);
            int maxEarn = parseEarn(earn, earnMaxAmt, DEFAULT_MAX_EARN);
            int minEarn = parseEarn(earn, earnMinAmt, DEFAULT_MIN_EARN);

            return Policy.builder()
                    .policyNum(plcyNo)
                    .region(region)
                    .title(plcyNm)
                    .institutionType(InstitutionType.fromKey(plcyNo, pvsnInstGroupCd))
                    .isLimitedAge(isLimitedAge)
                    .minAge(minAge)
                    .maxAge(maxAge)
                    .repeatCode(repeatCode)
                    .applyTerm(aplyYmd)
                    .applyStart(applyStart)
                    .applyDue(applyDue)
                    .addition(invalidToNull(addAplyQlfcCndCn))
                    .etc(invalidToNull(etcMttrCn))
                    .applLimit(invalidToNull(ptcpPrpTrgtCn))
                    .applStep(invalidToNull(plcyAplyMthdCn))
                    .applUrl(invalidToNull(aplyUrlAddr))
                    .category(Category.fromKey(plcyNo, bscPlanPlcyWayNo))
                    .education(Education.findEducationList(plcyNo, schoolCd))
                    .employment(Employment.findEmploymentList(plcyNo, jobCd))
                    .hostDep(invalidToNull(sprvsnInstCdNm))
                    .refUrl1(invalidToNull(refUrlAddr1))
                    .refUrl2(invalidToNull(refUrlAddr2))
                    .evaluation(invalidToNull(srngMthdCn))
                    .introduction(invalidToNull(plcyExplnCn))
                    .operatingOrg(invalidToNull(operInstCdNm))
                    .specialization(Specialization.findSpecializationList(plcyNo, sbizCd))
                    .major(Major.findMajorList(plcyNo, plcyMajorCd))
                    .submitDoc(invalidToNull(sbmsnDcmntCn))
                    .supportDetail(invalidToNull(plcySprtCn))
                    .earn(earn)
                    .maxEarn(maxEarn)
                    .minEarn(minEarn)
                    .earnEtc(invalidToNull(earnEtcCn))
                    .marriage(Marriage.fromKey(plcyNo, mrgSttsCd))
                    .zipCd(invalidToNull(zipCd))
                    .bizStart(bizTerm[0])
                    .bizDue(bizTerm[1])
                    .department(department)
                    .hostDepCode(invalidToNull(sprvsnInstCd))
                    .build();
        }catch (NullPointerException e){
            throw e;
        }
    }

    // 지역 코드 매핑
    private Region findRegion(){
        if(this.pvsnInstGroupCd.equals(regionCode)){ // 지자체 타입인 경우
            return Optional.ofNullable(Region.fromKey(rgtrHghrkInstCd)) // 등록자 최상위 코드 우선 검사
                    .or(() -> Optional.ofNullable(Region.fromKey(sprvsnInstCd))) // 주관 기관 코드 검사
                    //.orElseThrow(() -> new RuntimeException("Region not found : " + plcyNo));
                    .orElse(null);
        }
        else{
            return Region.ALL; // 이외는 중앙부처 처리
        }
    }

    private boolean isEmptyApplyYmd(){
        return !aplyPrdSeCd.equals("0057001") || aplyYmd.isEmpty();
    }

    // 신청 기간 파싱
    private LocalDate[] parsingApplyYmd(String applyDate){
        if(!isEmptyApplyYmd()){
            String[] dates = applyDate.split(" ~ ");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate applyStart = LocalDate.parse(dates[0], formatter);
            LocalDate applyDue = LocalDate.parse(dates[1].substring(0, 8), formatter);
            return new LocalDate[]{applyStart, applyDue};
        }
        return new LocalDate[]{null, null};
    }

    // 사업 운영 기간 파싱
    private LocalDate[] parsingBizTerm(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate bizStart = bizPrdBgngYmd.isBlank() ? null : LocalDate.parse(bizPrdBgngYmd, formatter);
        LocalDate bizDue = bizPrdEndYmd.isBlank() ? null : LocalDate.parse(bizPrdEndYmd, formatter);
        return new LocalDate[]{bizStart, bizDue};
    }

    // 신청 기간이 여러 개인 경우, 같은 값들인지 아닌지 판단
    private boolean isEqualApplyDates(String[] dates){
        String compare = dates[0];
        for(String date : dates){
            if(!compare.equals(date)){
                return false;
            }
            compare = date;
        }
        return true;
    }

    private String invalidToNull(String value){
        if (value != null) {
            String str = value.trim();
            if((str.isBlank() || INVALID_STRINGS.contains(str))){
                return null;
            }
        }
        return value;
    }

    private int parseAge(Boolean isLimitedAge, String value, int defaultValue) {
        return Boolean.TRUE.equals(isLimitedAge) && value != null && !value.isEmpty()
                ? Integer.parseInt(value)
                : defaultValue;
    }

    private int parseEarn(Earn earn, String value, int defaultValue) {
        return earn.equals(Earn.ANNUL_INCOME) && value != null && !earnMaxAmt.isEmpty()
                ? Integer.parseInt(value) : defaultValue;
    }
}
