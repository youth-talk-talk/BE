package com.server.youthtalktalk.service.policy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.domain.policy.Region;
import com.server.youthtalktalk.dto.policy.PolicyData;
import com.server.youthtalktalk.dto.policy.PolicyDataListResponse;
import com.server.youthtalktalk.repository.policy.PolicyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PolicyDataServiceImpl implements PolicyDataService {

    private final PolicyRepository policyRepository;
    @Value("${youthpolicy.api.secret-key}")
    private String secretKey;
    private int pageSize = 1;

    @Override
    @Transactional
    public void saveData() {
        Region[] regions = Region.values();
        List<Policy> policyList = new ArrayList<>();
        // 지역별로 데이터 저장
        for(Region region : regions) {
            List<PolicyData> dataList = fetchData(region.getKey());
            for(PolicyData data : dataList)
                policyList.add(data.toPolicy(region));
        }

        policyRepository.saveAll(policyList);
    }

    @Override
    public List<PolicyData> fetchData(String regionCode) {
        List<PolicyData> dataList = new ArrayList<>();

        WebClient webClient = WebClient.builder().baseUrl("https://www.youthcenter.go.kr/").build();
        int pageIndex = 1;
        while(pageIndex<=3){
            // api 호출 response 받기
            int page = pageIndex;
            Mono<String> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/opi/youthPlcyList.do")
                            .queryParam("openApiVlak", secretKey)
                            .queryParam("display", pageSize)
                            .queryParam("pageIndex", page)
                            .queryParam("srchPolyBizSecd", regionCode)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class);
            // xml 파싱
            ObjectMapper xmlMapper = new XmlMapper();
            try {
                PolicyDataListResponse dataListResponse = xmlMapper.readValue(response.block(), PolicyDataListResponse.class);
                if(dataListResponse.getYouthPolicies().isEmpty()) // 더 이상 페이지가 없으면 break
                    break;
                dataList.addAll(dataListResponse.getYouthPolicies()); // 데이터리스트에 추가
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
            pageIndex++;
        }
        return dataList;
    }

}
