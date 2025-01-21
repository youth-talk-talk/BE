package com.server.youthtalktalk.domain.policy.dto.data;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.entity.Region;
import com.server.youthtalktalk.domain.policy.entity.RepeatCode;
import com.server.youthtalktalk.global.util.DateExtractor;
import com.server.youthtalktalk.global.util.EmploymentStatusClassifier;
import com.server.youthtalktalk.global.util.UrlUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Optional;

import static com.server.youthtalktalk.global.util.InvalidValueFilter.filterInvalidValue;


@Slf4j
@Getter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class PolicyData {
    @JacksonXmlProperty(localName = "rnum")
    private String rnum;
    @JacksonXmlProperty(localName = "bizId")
    private String bizId;
    @JacksonXmlProperty(localName = "polyBizSecd")
    private String polyBizSecd;
    @JacksonXmlProperty(localName = "polyBizTy")
    private String polyBizTy;
    @JacksonXmlProperty(localName = "polyBizSjnm")
    private String polyBizSjnm;
    @JacksonXmlProperty(localName = "polyItcnCn")
    private String polyItcnCn;
    @JacksonXmlProperty(localName = "sporCn")
    private String sporCn;
    @JacksonXmlProperty(localName = "sporScvl")
    private String sporScvl;
    @JacksonXmlProperty(localName = "bizPrdCn")
    private String bizPrdCn;
    @JacksonXmlProperty(localName = "prdRpttSecd")
    private String prdRpttSecd;
    @JacksonXmlProperty(localName = "rqutPrdCn")
    private String rqutPrdCn;
    @JacksonXmlProperty(localName = "ageInfo")
    private String ageInfo;
    @JacksonXmlProperty(localName = "majrRqisCn")
    private String majrRqisCn;
    @JacksonXmlProperty(localName = "empmSttsCn")
    private String empmSttsCn;
    @JacksonXmlProperty(localName = "splzRlmRqisCn")
    private String splzRlmRqisCn;
    @JacksonXmlProperty(localName = "accrRqisCn")
    private String accrRqisCn;
    @JacksonXmlProperty(localName = "prcpCn")
    private String prcpCn;
    @JacksonXmlProperty(localName = "aditRscn")
    private String aditRscn;
    @JacksonXmlProperty(localName = "prcpLmttTrgtCn")
    private String prcpLmttTrgtCn;
    @JacksonXmlProperty(localName = "rqutProcCn")
    private String rqutProcCn;
    @JacksonXmlProperty(localName = "pstnPaprCn")
    private String pstnPaprCn;
    @JacksonXmlProperty(localName = "jdgnPresCn")
    private String jdgnPresCn;
    @JacksonXmlProperty(localName = "rqutUrla")
    private String rqutUrla;
    @JacksonXmlProperty(localName = "rfcSiteUrla1")
    private String rfcSiteUrla1;
    @JacksonXmlProperty(localName = "rfcSiteUrla2")
    private String rfcSiteUrla2;
    @JacksonXmlProperty(localName = "mngtMson")
    private String mngtMson;
    @JacksonXmlProperty(localName = "mngtMrofCherCn")
    private String mngtMrofCherCn;
    @JacksonXmlProperty(localName = "cherCtpcCn")
    private String cherCtpcCn;
    @JacksonXmlProperty(localName = "cnsgNmor")
    private String cnsgNmor;
    @JacksonXmlProperty(localName = "tintCherCn")
    private String tintCherCn;
    @JacksonXmlProperty(localName = "tintCherCtpcCn")
    private String tintCherCtpcCn;
    @JacksonXmlProperty(localName = "etct")
    private String etct;
    @JacksonXmlProperty(localName = "polyRlmCd")
    private String polyRlmCd;

    public Policy toPolicy() {
        log.info("bizId: {}, polyBizSecd: {}, polyBizTy: {}", bizId, polyBizSecd, polyBizTy);
        Region region = isNotSpecificRegion() ? Region.ALL : Region.fromKey(this.polyBizSecd.substring(0, 9));

        // 카테고리 분류
        Category category = switch (this.polyRlmCd) {
            case "023010" -> Category.JOB;
            case "023020", "023040" -> Category.LIFE;
            case "023030" -> Category.EDUCATION;
            case "023050" -> Category.PARTICIPATION;
            default -> null;
        };

        // 연령정보 분류
        int minAge = 0;
        int maxAge = 100;
        String ageInfo = cDataConvert(this.ageInfo);
        if(ageInfo!=null){
            String[] ages = ageInfo.replaceAll("[^0-9~]", "").split("~");
            if (ages.length == 1 && ageInfo.contains("이상")){
                minAge = Integer.parseInt(ages[0].trim());
            }
            else if (ages.length == 1 && ageInfo.contains("이하")){
                maxAge = Integer.parseInt(ages[0].trim());
            }
            else if (ages.length == 2){
                minAge = Integer.parseInt(ages[0].trim());
                maxAge = Integer.parseInt(ages[1].trim());
            }
        }

        // 반복코드 분류
        RepeatCode repeatCode = switch(this.prdRpttSecd) {
            case "002001" -> RepeatCode.ALWAYS;
            case "002002" -> RepeatCode.ANNUALLY;
            case "002003" -> RepeatCode.MONTHLY;
            case "002004" -> RepeatCode.PERIOD;
            case "002005" -> RepeatCode.UNDEFINED;
            default -> null;
        };

        // 날짜 데이터 전처리
        DateExtractor dateExtractor = new DateExtractor();
        String applyTerm = Optional
                .ofNullable(this.rqutPrdCn)
                .map(String::trim) // null이 아니면 공백 제거
                .orElseGet(()->"-"); // null은 "-"으로 대체
        LocalDate applyDue = dateExtractor.extractDue(applyTerm);

        // 취업상태 코드 분류
        String employment = filterInvalidValue(cDataConvert(this.empmSttsCn));
        String employmentCode = EmploymentStatusClassifier.classify(employment != null ? employment : "");

        // 지원 사이트 전처리
        String applUrl = filterInvalidValue(cDataConvert(this.rqutUrla));
        String formattedApplUrl = UrlUtil.formatUrl(cDataConvert(this.rqutUrla));

        // 참고 사이트 전처리
        String formattedRefUrl1 = UrlUtil.formatUrl(cDataConvert(this.rfcSiteUrla1));
        String formattedRefUrl2 = UrlUtil.formatUrl(cDataConvert(this.rfcSiteUrla2));

        // 추가 사항 전처리
        String addition = filterInvalidValue(cDataConvert(this.aditRscn));

        // 거주지 및 소득 조건 전처리
        String addrIncome = filterInvalidValue(cDataConvert(this.prcpCn));

        // 참여 제한 대상 전처리
        String applLimit = filterInvalidValue(cDataConvert(this.prcpLmttTrgtCn));

        // 신청 절차 전처리
        String applStep = filterInvalidValue(cDataConvert(this.rqutProcCn));

        // 학력 요건 전처리
        String education = filterInvalidValue(cDataConvert(this.accrRqisCn));

        // 기타 사항 전처리
        String etc = filterInvalidValue(cDataConvert(this.etct));

        // 심사 발표 전처리
        String evaluation = filterInvalidValue(cDataConvert(this.jdgnPresCn));

        // 전공 요건 전처리
        String major = filterInvalidValue(cDataConvert(this.majrRqisCn));

        // 정책 소개 전처리
        String introduction = filterInvalidValue(cDataConvert(this.polyItcnCn));

        // 운영기간 전처리
        String operationTerm = filterInvalidValue(this.bizPrdCn);

        // 주관 기관명 전처리
        String hostDep = filterInvalidValue(cDataConvert(this.mngtMson));

        // 운영 기관명 전처리
        String operatingOrg = filterInvalidValue(cDataConvert(this.cnsgNmor));

        // 특화 분야 전처리
        String specialization = filterInvalidValue(cDataConvert(this.splzRlmRqisCn));

        // 제출 서류 전처리
        String submitDoc = filterInvalidValue(cDataConvert(this.pstnPaprCn));

        // Policy 객체 생성
        return Policy.builder()
                .policyId(cDataConvert(this.bizId))
                .region(region)
                .title(cDataConvert(this.polyBizSjnm))
                .minAge(minAge)
                .maxAge(maxAge)
                .repeatCode(repeatCode)
                .applyTerm(applyTerm)
                .operationTerm(operationTerm)
                .applyDue(applyDue)
                .addition(addition)
                .etc(etc)
                .addrIncome(addrIncome)
                .applLimit(applLimit)
                .applStep(applStep)
                .applUrl(applUrl)
                .formattedApplUrl(cDataConvert(formattedApplUrl))
                .category(category)
                .education(education)
                .employment(employment)
                .employmentCode(employmentCode)
                .hostDep(hostDep)
                .refUrl1(formattedRefUrl1)
                .refUrl2(formattedRefUrl2)
                .evaluation(evaluation)
                .introduction(introduction)
                .operatingOrg(operatingOrg)
                .specialization(specialization)
                .major(major)
                .submitDoc(submitDoc)
                .supportDetail(cDataConvert(this.sporCn))
                .build();
    }

    public boolean isNotSpecificRegion(){
        return this.polyBizTy == null || this.polyBizTy.equals("중앙부처");
    }

    public String cDataConvert(String value){
        if (value != null && value.startsWith("<![CDATA[") && value.endsWith("]]>")) {
            return value.substring(9, value.length() - 3);
        }
        return value;
    }

}