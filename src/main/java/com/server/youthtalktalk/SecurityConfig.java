/*
package com.server.youthtalktalk;

import com.server.youthtalktalk.exception.Exception401;
import com.server.youthtalktalk.exception.Exception403;
import com.server.youthtalktalk.security.JwtAuthenticationFilter;
import com.server.youthtalktalk.util.FilterResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.http.SessionCreationPolicy;

@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 비밀번호 인코더를 생성합니다.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // 인증 관리자 빈을 생성합니다.
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())) // 동일 출처에서의 iframe 허용
                .cors(cors -> cors.configurationSource(configurationSource())) // CORS 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 정책을 STATELESS로 설정
                .formLogin(form -> form.disable()) // 기본 폼 로그인 비활성화
                .httpBasic(basic -> basic.disable()) // HTTP Basic 인증 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health").permitAll() // Actuator 헬스 체크 엔드포인트 접근 허용
                        .requestMatchers("/carts/**", "/options/**", "/orders/**").authenticated() // 인증 필요 경로
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 관리자 역할 필요 경로
                        .anyRequest().permitAll()) // 나머지 모든 경로 허용
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> response.sendRedirect("/"))
                        .deleteCookies("jwtToken")) // 로그아웃 설정
                .addFilter(new JwtAuthenticationFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)), jwtSecret)) // JWT 인증 필터 추가
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.warn("인증되지 않은 사용자가 자원에 접근하려 합니다: " + authException.getMessage());
                            FilterResponseUtils.unAuthorized(response, new Exception401("인증되지 않았습니다")); // 인증되지 않은 사용자 처리
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.warn("권한이 없는 사용자가 자원에 접근하려 합니다: " + accessDeniedException.getMessage());
                            FilterResponseUtils.forbidden(response, new Exception403("권한이 없습니다")); // 권한이 없는 사용자 처리
                        }));

        return http.build(); // SecurityFilterChain 반환
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader("*"); // 모든 헤더 허용
        corsConfiguration.addAllowedMethod("*"); // 모든 HTTP 메소드 허용
        corsConfiguration.addAllowedOriginPattern("*"); // 모든 출처 허용
        corsConfiguration.setAllowCredentials(true); // 자격 증명 허용
        corsConfiguration.addExposedHeader("Authorization"); // 노출할 헤더 설정

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration); // CORS 설정 적용 경로 등록
        return source;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // 인메모리 사용자 상세 서비스 설정
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername(username).password(passwordEncoder().encode(password)).roles("USER").build()); // 사용자 생성
        return manager;
    }
}*/
