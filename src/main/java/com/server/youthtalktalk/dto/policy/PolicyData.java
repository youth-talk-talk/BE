package com.server.youthtalktalk.dto.policy;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.server.youthtalktalk.domain.policy.Category;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.domain.policy.Region;
import com.server.youthtalktalk.domain.policy.RepeatCode;
import com.server.youthtalktalk.global.util.DateExtractor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    public Policy toPolicy(Region region) {
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
        int maxAge = 0;
        String ageInfo = cDataConvert(this.ageInfo);
        if(ageInfo!=null&&!ageInfo.equals("제한없음")){
            String[] ages = ageInfo.replaceAll("[^0-9~]", "").split("~");
            minAge = Integer.parseInt(ages[0].trim());
            if(ages.length>=2) maxAge = Integer.parseInt(ages[1].trim());
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

        /**
         * TO-DO 신청기간에서 마감일 추출
         */
        DateExtractor dateExtractor = new DateExtractor();
        String applyTerm = Optional
                .ofNullable(this.rqutPrdCn)
                .map(String::trim) // null이 아니면 공백 제거
                .orElseGet(()->"-"); // null은 "-"으로 대체
        LocalDate applyDue = dateExtractor.extractDue(applyTerm);
        log.info("applyTerm={}", applyTerm);
        log.info("applyDue={}", applyDue);

        return Policy.builder()
                .policyId(cDataConvert(this.bizId))
                .region(region)
                .title(cDataConvert(this.polyBizSjnm))
                .minAge(minAge)
                .maxAge(maxAge)
                .repeatCode(repeatCode)
                .applyTerm(applyTerm)
                .operationTerm(this.bizPrdCn)
                .applyDue(applyDue)
                .addition(cDataConvert(this.aditRscn))
                .etc(cDataConvert(this.etct))
                .addrIncome(cDataConvert(this.prcpCn))
                .applLimit(cDataConvert(this.prcpLmttTrgtCn))
                .applStep(cDataConvert(this.rqutProcCn))
                .applUrl(cDataConvert(this.rqutUrla))
                .category(category)
                .education(cDataConvert(this.accrRqisCn))
                .employment(extractEmployment(cDataConvert(this.empmSttsCn)))
                .hostDep(cDataConvert(this.mngtMson))
                .refUrl1(cDataConvert(this.rfcSiteUrla1))
                .refUrl2(cDataConvert(this.rfcSiteUrla2))
                .evaluation(cDataConvert(this.jdgnPresCn))
                .introduction(cDataConvert(this.polyItcnCn))
                .operatingOrg(cDataConvert(this.cnsgNmor))
                .specialization(cDataConvert(this.splzRlmRqisCn))
                .submitDoc(cDataConvert(this.pstnPaprCn))
                .supportDetail(cDataConvert(this.sporCn))
                .build();
    }

    public String cDataConvert(String value){
        if (value != null && value.startsWith("<![CDATA[") && value.endsWith("]]>")) {
            return value.substring(9, value.length() - 3);
        }
        return value;
    }

    public List<String> extractEmployment(String employment){
        // 쉼표 기준으로 문자열을 분리하고, 양쪽 공백 제거
        return Arrays.asList(employment.split("\\s*,\\s*"));
    }
}