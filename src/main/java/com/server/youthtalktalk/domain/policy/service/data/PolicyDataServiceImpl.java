package com.server.youthtalktalk.domain.policy.service.data;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.server.youthtalktalk.domain.policy.dto.data.DepartmentResponseDto;
import com.server.youthtalktalk.domain.policy.dto.data.PolicyData;
import com.server.youthtalktalk.domain.policy.dto.data.PolicyDataList;
import com.server.youthtalktalk.domain.policy.entity.Department;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.entity.region.PolicySubRegion;
import com.server.youthtalktalk.domain.policy.entity.region.Region;
import com.server.youthtalktalk.domain.policy.entity.region.SubRegion;
import com.server.youthtalktalk.domain.policy.repository.DepartmentRepository;
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
import reactor.core.scheduler.Schedulers;
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
    private final DepartmentRepository departmentRepository;
    private final WebClient webClient;

    @Value("${youthpolicy.api.secret-key}")
    private String policySecretKey;
    @Value("${department.api.secret-key}")
    private String departmentSecretKey;
    private static final String DEFAULT_DEPARTMENT = "0";
    private static final int PAGE_SIZE = 150;
    private static final int LIMIT = 1000;

    @Override
    @Transactional
    @Scheduled(cron = "${youthpolicy.cron}")
    public void saveData() {
        log.info("[온통청년 Data Fetch] 정책 데이터 패치 시작");
        List<PolicyData> policyDataList = fetchPolicyData();
        List<Policy> policyList = getPolicyEntityList(policyDataList).block();

        log.info("[온통청년 Data Fetch] 패치된 정책 데이터 DB 저장");
        assert policyList != null;
        List<Policy> savedPolicyList = policyRepository.saveAll(policyList);
        log.info("[온통청년 Data Fetch] SubRegion과 매핑");
        policySubRegionRepository.deleteAllByPolicyIn(savedPolicyList);

        // 하위 지역 코드 매핑
        List<PolicySubRegion> policySubRegionList = new ArrayList<>();
        savedPolicyList.stream()
                .filter(policy -> !policy.getRegion().equals(Region.CENTER))
                .forEach(policy -> policySubRegionList.addAll(setPolicySubRegions(policy)));
        policySubRegionRepository.saveAll(policySubRegionList);
    }

    @Override
    public Mono<List<Policy>> getPolicyEntityList(List<PolicyData> policyDataList) {
        Department defaultDepartment = departmentRepository.findByCode(DEFAULT_DEPARTMENT).get();
        // 정책 데이터를 비동기적으로 처리
        return Flux.fromIterable(policyDataList)
                .delayElements(Duration.ofMillis(50))
                .parallel(50)  // 최대 동시 실행 개수 10개 제한
                .runOn(Schedulers.boundedElastic()) // I/O 작업 최적화
                .flatMap(policyData -> Mono.fromCallable(() -> {
                    try {
                        // 기존 정책이 있는지 확인 (policyNum 기준)
                        Optional<Policy> existingPolicy = policyRepository.findByPolicyNum(policyData.plcyNo());
                        // 중앙 부처 코드 매핑
                        String departmentCode = policyData.sprvsnInstCd();
                        String departmentName = policyData.sprvsnInstCdNm();

                        Department department;
                        // 이미 존재하는 정책이고, 기존 주관 기관이 변경되지 않으면 외부 API 호출 X
                        if(existingPolicy.isPresent() && isExistedDepartment(existingPolicy.get(), departmentCode)) {
                            department = existingPolicy.get().getDepartment();
                        }
                        else if(departmentCode.isBlank()) {
                            department = defaultDepartment;
                        }
                        else{
                            department = departmentRepository.findByCode(departmentCode)
                                    .orElseGet(() -> searchDepartmentCode(departmentCode, departmentName, defaultDepartment));
                        }

                        Policy policy = policyData.toPolicy(department);

                        // 지역이 설정되지 않은 경우
                        if (policy.getRegion() == null) {
                            policy = setRegionForPolicy(policy);
                        }

                        if (existingPolicy.isPresent()) {
                            // 기존 정책이 있으면 업데이트
                            Policy existing = existingPolicy.get();
                            // 필드 업데이트 (toBuilder()를 통해 기존 객체를 기반으로 업데이트)
                            return existing.toBuilder()
                                    .title(policy.getTitle())
                                    .department(policy.getDepartment())
                                    .region(policy.getRegion())
                                    .zipCd(policy.getZipCd())
                                    .supportDetail(policy.getSupportDetail())
                                    .introduction(policy.getIntroduction())
                                    .applyTerm(policy.getApplyTerm())
                                    .applyStart(policy.getApplyStart())
                                    .applyDue(policy.getApplyDue())
                                    .minAge(policy.getMinAge())
                                    .maxAge(policy.getMaxAge())
                                    .addition(policy.getAddition())
                                    .applLimit(policy.getApplLimit())
                                    .applStep(policy.getApplStep())
                                    .submitDoc(policy.getSubmitDoc())
                                    .evaluation(policy.getEvaluation())
                                    .applUrl(policy.getApplUrl())
                                    .refUrl1(policy.getRefUrl1())
                                    .refUrl2(policy.getRefUrl2())
                                    .hostDep(policy.getHostDep())
                                    .operatingOrg(policy.getOperatingOrg())
                                    .etc(policy.getEtc())
                                    .category(policy.getCategory())
                                    .isLimitedAge(policy.getIsLimitedAge())
                                    .earn(policy.getEarn())
                                    .minEarn(policy.getMinEarn())
                                    .maxEarn(policy.getMaxEarn())
                                    .earnEtc(policy.getEarnEtc())
                                    .zipCd(policy.getZipCd())
                                    .specialization(policy.getSpecialization())
                                    .major(policy.getMajor())
                                    .education(policy.getEducation())
                                    .marriage(policy.getMarriage())
                                    .employment(policy.getEmployment())
                                    .bizStart(policy.getBizStart())
                                    .bizDue(policy.getBizDue())
                                    .build();
                        } else {
                            // 기존 정책이 없으면 새로 추가
                            return policy;
                        }

                    } catch (Exception e) {
                        // 예외 발생 시 null 반환하여 필터링
                        return null;
                    }
                }))
                .sequential()
                .filter(Objects::nonNull) // null인 항목은 필터링하여 제거
                .collectList(); // 결과를 List로 모아서 반환
    }

    @Override
    public List<PolicyData> fetchPolicyData() {
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
                                .queryParam("apiKeyNm", policySecretKey)
                                .queryParam("pageSize", PAGE_SIZE)
                                .queryParam("pageNum", pageNum)
                                .queryParam("rtnType", "json")
                                .queryParam("pageType", "1")
                                .build())
                        .retrieve()
                        .bodyToMono(PolicyDataList.class)
                        .retryWhen(Retry.backoff(5, Duration.ofSeconds(2))
                                .doBeforeRetry(before -> log.info("[온통청년 Data Fetch] Retry 시도 : {}", before.toString()))
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
                throw new RuntimeException("[온통청년 Data Fetch] policyDataList가 존재하지 않습니다.");
            }

            List<PolicyData> youthPolicies = Optional.ofNullable(policyDataList.result().youthPolicyList())
                    .orElse(Collections.emptyList());

            if (youthPolicies.isEmpty()) {
                log.info("[온통청년 Data Fetch] 더 이상 데이터가 없습니다. 패치 종료");
                break;
            }
            dataList.addAll(youthPolicies);
            pageIndex++;
        }

        return dataList;
    }

    @Override
    public Department searchDepartmentCode(String departmentCode, String departmentName, Department defaultDepartment) {
        String name[] = departmentName.split(",");
        Optional<Department> department = departmentRepository.findByName(name[0]);
        if(department.isPresent()){
            return department.get();
        }
        // 해당 코드의 상위 기관 코드 탐색
        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/1741000/StanOrgCd2/getStanOrgCdList2")
                            .queryParam("ServiceKey", departmentSecretKey)
                            .queryParam("type", "xml")
                            .queryParam("pageNo", 1)
                            .queryParam("numOfRows", 1)
                            .queryParam("org_cd", departmentCode)
                            .build())
                    .retrieve().bodyToMono(String.class)
                    .block();

            XmlMapper xmlMapper = new XmlMapper();
            DepartmentResponseDto data = xmlMapper.readValue(response, DepartmentResponseDto.class);
            //"row" 항목만 추출
            String representativeCode = data.row().get(0).repCd();
            return departmentRepository.findByCode(representativeCode).orElse(defaultDepartment);

        } catch (Exception e) {
            return defaultDepartment;
        }
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
                return Region.CENTER;
            }
            return region;
        }
        return Region.CENTER;
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

    private boolean isExistedDepartment(Policy policy, String departmentCode) {
        return policy.getHostDepCode().equals(departmentCode);
    }
}
