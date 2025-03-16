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
import reactor.core.publisher.Flux;
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
    private static final int PAGE_SIZE = 50;
    private static final int LIMIT = 1000;

    @Override
    @Transactional
    @Scheduled(cron = "${youthpolicy.cron}")
    public void saveData() {
        log.info("[온통청년 Data Fetch] Data fetch start");
        List<PolicyData> policyDataList = fetchData();
        List<Policy> policyList = getPolicyEntityList(policyDataList);

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
    public List<Policy> getPolicyEntityList(List<PolicyData> policyDataList) {
        return policyDataList.stream()
                .map(policyData -> {
                    try {
                        Policy policy = policyData.toPolicy(); // policy 생성
                        if (policy.getRegion() == null) { // 지역이 설정되지 않은 경우
                            policy = setRegionForPolicy(policy); // 지역 설정 로직을 메서드로 분리
                        }
                        return policy; // 정상적으로 생성된 policy 반환
                    } catch (Exception e) {
                        // 예외 발생 시 로깅하거나 예외를 처리하고, null을 반환하여 리스트에 추가되지 않도록 함
                        return null;
                    }
                })
                .filter(Objects::nonNull) // null인 항목은 필터링하여 제거
                .toList();
    }

    @Override
    public List<PolicyData> fetchData() {
        List<PolicyData> dataList = new ArrayList<>();
        WebClient webClient = WebClient.builder()
                .baseUrl("https://www.youthcenter.go.kr/")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 16MB 설정
                //.filter(logRequest())
                .build();

        int pageIndex = 1;
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
                                .filter(throwable -> throwable instanceof WebClientResponseException))
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

    @Override
    public List<PolicySubRegion> setPolicySubRegions(Policy policy) {
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

    @Override
    public Region searchRegionByZipCd(Policy policy){
        String[] regionCodes = policy.getZipCd().split(",");
        if(!regionCodes[0].isBlank()){
            Region region = Region.fromNum(Integer.valueOf(regionCodes[0].substring(0,2)));
            if(region == null){
                return Region.ALL;
            }
            return region;
        }
        return Region.ALL;
    }

    private Policy setRegionForPolicy(Policy policy) {
        Region region = searchRegionByZipCd(policy);
        return policy.toBuilder().region(region).build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request URL: {}", clientRequest.url()); // 요청 URL + 파라미터 출력
            return Mono.just(clientRequest);
        });
    }
}