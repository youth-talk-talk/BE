package com.server.youthtalktalk.domain.policy.service.data;

import com.server.youthtalktalk.domain.policy.dto.data.PolicyData;
import com.server.youthtalktalk.domain.policy.dto.data.PolicyDataList;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.entity.region.PolicySubRegion;
import com.server.youthtalktalk.domain.policy.entity.region.Region;
import com.server.youthtalktalk.domain.policy.entity.region.SubRegion;
import com.server.youthtalktalk.domain.policy.repository.PolicyRepository;
import com.server.youthtalktalk.domain.policy.repository.region.PolicySubRegionRepository;
import com.server.youthtalktalk.domain.policy.repository.region.SubRegionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyDataServiceImpl implements PolicyDataService {
    private final PolicyRepository policyRepository;
    private final PolicySubRegionRepository policySubRegionRepository;
    private final SubRegionRepository subRegionRepository;

    @Value("${youthpolicy.api.secret-key}")
    private String secretKey;
    private int pageSize = 100;
    private static final String regionCode = "0054002";

    @Override
    @Transactional
    @Scheduled(cron = "${youthpolicy.cron}")
    public void saveData() {
        List<PolicyData> policyDataList = fetchData();
        List<Policy> policyList = policyDataList.stream()
                .map(policyData -> {
                    Policy policy = policyData.toPolicy();
                    if (policy.getRegion() == null) { // 지역이 설정되지 않은 경우
                        policy = setRegionForPolicy(policy); // 지역 설정 로직을 메서드로 분리
                    }
                    return policy;
                })
                .toList();
//        List<Policy> savedPolicyList = policyRepository.saveAll(policyList);
//
//        policySubRegionRepository.deleteAllByPolicyIn(policyList);
//        // 하위 지역 코드 매핑
//        List<PolicySubRegion> policySubRegionList = new ArrayList<>();
//        savedPolicyList.stream()
//                .filter(policy -> !policy.getRegion().equals(Region.ALL))
//                .forEach(policy -> policySubRegionList.addAll(setPolicySubRegions(policy)));
//        policySubRegionRepository.saveAll(policySubRegionList);
    }

    @Override
    public List<PolicyData> fetchData() {
        List<PolicyData> dataList = new ArrayList<>();
        WebClient webClient = WebClient.builder()
                .baseUrl("https://www.youthcenter.go.kr/")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))  // 예: 16MB
                .build();

        int pageIndex = 1; // 페이지 인덱스는 1부터 시작
        boolean hasMoreData = true;
        while(hasMoreData){
            // api 호출 response 받기
            int pageNum = pageIndex;
            Mono<PolicyDataList> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/go/ythip/getPlcy")
                            .queryParam("apiKeyNm", secretKey)
                            .queryParam("pageSize", pageSize)
                            .queryParam("pageNum", pageNum)
                            .queryParam("rtnType", "json")
                            .queryParam("pageType", "1") // 목록 출력
                            .build())
                    .retrieve()
                    .bodyToMono(PolicyDataList.class);
            // 정책 리스트 추출
            List<PolicyData> youthPolicies = Optional
                    .ofNullable(response.block().result().youthPolicyList())
                    .orElse(Collections.emptyList());

            if (youthPolicies.isEmpty()) {
                log.info("No more policies available");
                hasMoreData=false;
            }
            dataList.addAll(youthPolicies);
            pageIndex++;
        }
        return dataList;
    }


    private List<PolicySubRegion> setPolicySubRegions(Policy policy) {
        // zipCd 거주 지역 코드 파싱
        String[] regionCodes = policy.getZipCd().split(",");
        List<String> codeList = Arrays.stream(regionCodes).toList();

        List<SubRegion> subRegionList = subRegionRepository.findAllByRegionAndCodeIn(policy.getRegion(), codeList);
        List<PolicySubRegion> policySubRegionList = new ArrayList<>();

        for(SubRegion subRegion : subRegionList){
            PolicySubRegion policySubRegion = PolicySubRegion.builder().build();
            policySubRegion.setPolicy(policy);
            policySubRegion.setSubRegion(subRegion);
            policySubRegionList.add(policySubRegion);
        }

        return policySubRegionList;
    }

    private Region searchRegionByZipCd(Policy policy){
        String[] regionCodes = policy.getZipCd().split(",");
        if(!regionCodes[0].isBlank()){
            SubRegion subRegion = subRegionRepository.findByCode(regionCodes[0])
                    .orElseThrow(() -> new RuntimeException("Not Existed Region Code"));
            return subRegion.getRegion();
        }
        return null;
    }

    private Policy setRegionForPolicy(Policy policy) {
        Region region = searchRegionByZipCd(policy);
        if (region == null) {
            return policy.toBuilder().region(Region.ALL).build();
        } else {
            return policy.toBuilder().region(region).build();
        }
    }

}