package com.server.youthtalktalk.domain.policy.dto.data;

import com.server.youthtalktalk.domain.policy.entity.*;
import com.server.youthtalktalk.domain.policy.entity.condition.*;
import com.server.youthtalktalk.domain.policy.entity.region.Region;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
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

    public Policy toPolicy(){
        try{
            // 신청 기간 파싱(상시인 경우 x)
            if(aplyYmd.length() > 20){
                log.info("aplyYmd policyId = {}", plcyNo);
            }
            LocalDate[] applyDates = parsingApplyYmd();
            LocalDate applyStart = applyDates[0];
            LocalDate applyDue = applyDates[1];

            Earn earn = Earn.fromKey(plcyNo, earnCndSeCd);
            boolean isLimitedAge = sprtTrgtAgeLmtYn.equals("N");
            Region region = findRegion();


            return Policy.builder()
                    .policyId(plcyNo)
                    .region(region)
                    .title(plcyNm)
                    .isLimitedAge(isLimitedAge)
                    .minAge(isLimitedAge && !sprtTrgtMinAge.isEmpty() ? Integer.parseInt(sprtTrgtMinAge) : 0)
                    .maxAge(isLimitedAge && !sprtTrgtMaxAge.isEmpty() ? Integer.parseInt(sprtTrgtMaxAge) : 0)
                    .repeatCode(RepeatCode.fromKey(plcyNo, aplyPrdSeCd))
                    .applyStart(applyStart)
                    .applyDue(applyDue)
                    .addition(addAplyQlfcCndCn)
                    .etc(etcMttrCn)
                    .applLimit(ptcpPrpTrgtCn)
                    .applStep(plcyAplyMthdCn)
                    .applUrl(aplyUrlAddr)
                    .category(Category.fromKey(plcyNo, bscPlanPlcyWayNo))
                    .education(Education.findEducationList(plcyNo, schoolCd))
                    .employment(Employment.findEmploymentList(plcyNo, jobCd))
                    .hostDep(sprvsnInstCdNm)
                    .refUrl1(refUrlAddr1)
                    .refUrl2(refUrlAddr2)
                    .evaluation(srngMthdCn)
                    .introduction(plcyExplnCn)
                    .operatingOrg(operInstCdNm)
                    .specialization(Specialization.findSpecializationList(plcyNo, sbizCd))
                    .major(Major.findMajorList(plcyNo, plcyMajorCd))
                    .submitDoc(sbmsnDcmntCn)
                    .supportDetail(plcySprtCn)
                    .subCategory(SubCategory.fromKey(plcyNo, bscPlanFcsAsmtNo))
                    .earn(earn)
                    .maxEarn(earn.equals(Earn.ANNUL_INCOME) && !earnMaxAmt.isEmpty() ? Integer.parseInt(earnMaxAmt) : 0)
                    .minEarn(earn.equals(Earn.ANNUL_INCOME) && !earnMinAmt.isEmpty() ? Integer.parseInt(earnMinAmt) : 0)
                    .earnEtc(earnEtcCn)
                    .marriage(Marriage.fromKey(plcyNo, mrgSttsCd))
                    .zipCd(zipCd)
                    .build();
        }catch (Exception e){
            log.info("policyId = {}", plcyNo, e);
            throw new RuntimeException(e);
        }
    }

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

    private LocalDate[] parsingApplyYmd(){
        if(!isEmptyApplyYmd()){
            String[] dates = this.aplyYmd.split(" ~ ");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate applyStart = LocalDate.parse(dates[0], formatter);
            LocalDate applyDue = LocalDate.parse(dates[1].substring(0, 8), formatter);
            return new LocalDate[]{applyStart, applyDue};
        }
        return new LocalDate[]{null, null};
    }
}
