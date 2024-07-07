package com.server.youthtalktalk.service.policy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.domain.policy.Region;
import com.server.youthtalktalk.dto.policy.PolicyData;
import com.server.youthtalktalk.dto.policy.PolicyDataListResponse;
import com.server.youthtalktalk.repository.PolicyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyDataServiceImpl implements PolicyDataService {

    private final PolicyRepository policyRepository;
    @Value("${youthpolicy.api.secret-key}")
    private String secretKey;
    private int pageSize = 50;

    @Override
    @Transactional
    public void saveData() {
        List<PolicyData> policyDataList = fetchData();
        List<Policy> policyList = policyDataList.stream().map(PolicyData::toPolicy).toList();
        policyRepository.saveAll(policyList);
    }

    @Override
    public List<PolicyData> fetchData() {
        List<PolicyData> dataList = new ArrayList<>();
        WebClient webClient = WebClient.builder().baseUrl("https://www.youthcenter.go.kr/").build();

        int pageIndex = 1; // 페이지 인덱스는 1부터 시작
        boolean hasMoreData = true;
        while(hasMoreData){
            // api 호출 response 받기
            int page = pageIndex;
            Mono<String> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/opi/youthPlcyList.do")
                            .queryParam("openApiVlak", secretKey)
                            .queryParam("display", pageSize)
                            .queryParam("pageIndex", page)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class);
            // xml 파싱
            ObjectMapper xmlMapper = new XmlMapper();
            try {
                PolicyDataListResponse dataListResponse = xmlMapper.readValue(response.block(), PolicyDataListResponse.class);
                List<PolicyData> youthPolicies = Optional
                        .ofNullable(dataListResponse.getYouthPolicies())
                        .orElse(Collections.emptyList());
                if (youthPolicies.isEmpty()) {
                    log.info("No more policies available");
                    hasMoreData=false;
                }
                dataList.addAll(youthPolicies);
                pageIndex++;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }
        return dataList;
    }

}