package com.server.youthtalktalk.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .formLogin(form -> form.disable()) // 기본 폼 로그인 비활성화
                .httpBasic(basic -> basic.disable()) // HTTP Basic 인증 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 정책을 STATELESS로 설정
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health").permitAll() // Actuator 헬스 체크 엔드포인트 접근 허용
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 관리자 역할 필요 경로
                        .anyRequest().permitAll()); // 나머지 모든 경로 허용

        return http.build();
    }

}
