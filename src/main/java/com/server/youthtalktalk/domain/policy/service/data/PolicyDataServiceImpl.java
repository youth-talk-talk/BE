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
import com.server.youthtalktalk.global.response.exception.policy.FailPolicyDataFetchException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
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
    private static final int PAGE_SIZE = 27;
    private static final int LIMIT = 500;

    @Override
    @Transactional
    @Scheduled(cron = "${youthpolicy.cron}")
    public void saveData() {
        log.info("[온통청년 Data Fetch] Data fetch start");
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

        log.info("[온통청년 Data Fetch] Fetched policies save to DB");
        List<Policy> savedPolicyList = policyRepository.saveAll(policyList);
        log.info("[온통청년 Data Fetch] Mapping with sub regions");
        policySubRegionRepository.deleteAllByPolicyIn(savedPolicyList);
        // 하위 지역 코드 매핑
        List<PolicySubRegion> policySubRegionList = new ArrayList<>();
        savedPolicyList.stream()
                .filter(policy -> !policy.getRegion().equals(Region.ALL))
                .forEach(policy -> policySubRegionList.addAll(setPolicySubRegions(policy)));
        policySubRegionRepository.saveAll(policySubRegionList);
    }

    @Override
    public List<PolicyData> fetchData() {
        List<PolicyData> dataList = new ArrayList<>();
        WebClient webClient = WebClient.builder()
                .baseUrl("https://www.youthcenter.go.kr/")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 16MB 설정
                .filter(logRequest())
                .build();

        int pageIndex = 1;
        boolean hasMoreData = true;
        while (pageIndex < LIMIT) {
            int pageNum = pageIndex;
            PolicyDataList policyDataList = null;
            try {
                Mono<PolicyDataList> response = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/go/ythip/getPlcy")
                                .queryParam("apiKeyNm", secretKey)
                                .queryParam("pageSize", PAGE_SIZE)
                                .queryParam("pageNum", pageNum)
                                .queryParam("rtnType", "json")
                                .queryParam("pageType", "1")
                                .build())
                        .retrieve()
                        .bodyToMono(PolicyDataList.class)
                        .retryWhen(Retry.backoff(5, Duration.ofSeconds(2))
                                .doBeforeRetry(before -> log.info("[온통청년 Data Fetch] Retry : {}", before.toString()))
                                .filter(throwable -> throwable instanceof WebClientResponseException
                                        && ((WebClientResponseException) throwable).getStatusCode().is5xxServerError()))
                        .onErrorResume(e -> {
                            log.error("[온통청년 Data Fetch] API 호출 실패: {}", e.getMessage());
                            throw new FailPolicyDataFetchException();
                        });
                policyDataList = response.block();
            } catch (Exception e) {
                throw new FailPolicyDataFetchException();
            }

            if(policyDataList == null || policyDataList.result() == null){
                throw new RuntimeException("[온통청년 Data Fetch] data fetch null error");
            }

            List<PolicyData> youthPolicies = Optional.ofNullable(policyDataList.result().youthPolicyList())
                    .orElse(Collections.emptyList());

            if (youthPolicies.isEmpty()) {
                log.info("[온통청년 Data Fetch] No more data found, loop break");
                break;
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

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request URL: {}", clientRequest.url()); // 요청 URL + 파라미터 출력
            return Mono.just(clientRequest);
        });
    }

}