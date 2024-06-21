package com.server.youthtalktalk.dto.policy;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.server.youthtalktalk.domain.policy.Category;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.domain.policy.Region;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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


    public Policy toPolicy(PolicyData data, Region region){
        // 카테고리 분류
        Category category = switch (data.getPolyRlmCd()) {
            case "023010" -> Category.JOB;
            case "023020", "023040" -> Category.LIFE;
            case "023030" -> Category.EDUCATION;
            case "023050" -> Category.PARTICIPATION;
            default -> null;
        };
        int minAge = 0;
        int maxAge = 0;

        // 연령정보 분류
        String ageInfo = cDataConvert(data.getAgeInfo());
        if(ageInfo!=null&&!ageInfo.equals("제한없음")){
            String[] ages = ageInfo.replaceAll("[^0-9~]", "").split("~");
            minAge = Integer.parseInt(ages[0].trim());
            if(ages.length>=2) maxAge = Integer.parseInt(ages[1].trim());
        }
        // 신청기간 분류
        String applTerm = cDataConvert(data.getRqutPrdCn());

        LocalDate applEndDate = null;
        // 파싱 코드

        return Policy.builder()
                .policyId(cDataConvert(data.getBizId()))
                .region(region)
                .title(cDataConvert(data.getPolyBizSjnm()))
                .minAge(minAge)
                .maxAge(maxAge)
                .applEndDate(applEndDate)
                .addition(cDataConvert(data.getAditRscn()))
                .etc(cDataConvert(data.getEtct()))
                .addrIncome(cDataConvert(data.getPrcpCn()))
                .applLimit(cDataConvert(data.getPrcpLmttTrgtCn()))
                .applStep(cDataConvert(data.getRqutProcCn()))
                .applUrl(cDataConvert(data.getRqutUrla()))
                .category(category)
                .education(cDataConvert(data.getAccrRqisCn()))
                .employment(extractEmployment(cDataConvert(data.getEmpmSttsCn())))
                .hostDep(cDataConvert(data.getMngtMson()))
                .refUrl1(cDataConvert(data.getRfcSiteUrla1()))
                .refUrl2(cDataConvert(data.getRfcSiteUrla2()))
                .evaluation(cDataConvert(data.getJdgnPresCn()))
                .introduction(cDataConvert(data.getPolyItcnCn()))
                .operatingOrg(cDataConvert(data.getCnsgNmor()))
                .specialization(cDataConvert(data.getSplzRlmRqisCn()))
                .submitDoc(cDataConvert(data.getPstnPaprCn()))
                .supportDetail(cDataConvert(data.getSporCn()))
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
