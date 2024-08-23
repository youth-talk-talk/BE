package com.server.youthtalktalk.global.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

public interface JwtService {
    String createAccessToken(String username);
    String createRefreshToken();
    void updateRefreshToken(String username, String refreshToken);
    void destroyRefreshToken(String username);
    void sendAccessToken(HttpServletResponse response, String accessToken);
    void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken);
    Optional<String> extractAccessToken(HttpServletRequest request);
    Optional<String> extractRefreshToken(HttpServletRequest request);
    Optional<String> extractUsername(String accessToken);
    void isTokenValid(String token);
}
