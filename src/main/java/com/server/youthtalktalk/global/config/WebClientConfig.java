package com.server.youthtalktalk.global.config;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Component
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
        ConnectionProvider provider = ConnectionProvider.builder("client")
                .maxConnections(100) // 최대 50개의 동시 연결 허용
                .pendingAcquireMaxCount(1000) // 대기 중인 요청 개수 증가
                .build();

        HttpClient httpClient = HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000); // 타임아웃 설정

        return WebClient.builder()
                .baseUrl("http://apis.data.go.kr/") // 기본 URL 설정
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
