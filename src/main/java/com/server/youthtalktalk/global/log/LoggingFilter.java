package com.server.youthtalktalk.global.log;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 클라이언트 아이피
        String clientId = request.getRemoteAddr();
        // 요청 메서드
        String method = request.getMethod();
        // 요청 API 경로
        String requestURI = request.getRequestURI();
        // User-Agent
        String agent = request.getHeader("User-Agent");
        // 응답 Status
        int status = response.getStatus();

        log.info("Client : {}, URI : {} {}, Agent : {}, Status : {}", clientId, requestURI, method, agent, status);

        filterChain.doFilter(request, response);
    }
}
