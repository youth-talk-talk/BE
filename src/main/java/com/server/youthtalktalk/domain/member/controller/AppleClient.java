package com.server.youthtalktalk.domain.member.controller;

import com.server.youthtalktalk.domain.member.dto.apple.AppleDto;
import com.server.youthtalktalk.global.config.FeignConfig;
import com.server.youthtalktalk.domain.member.dto.apple.ApplePublicKeyResponse;
import com.server.youthtalktalk.domain.member.dto.apple.AppleTokenResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.server.youthtalktalk.domain.member.dto.apple.AppleDto.*;

@FeignClient(name = "appleClient", url = "https://appleid.apple.com/auth", configuration = FeignConfig.class)
public interface AppleClient {

    @GetMapping(value = "/keys")
    ApplePublicKeyResponse findAppleAuthPublicKeys();

    @PostMapping(value = "/token", consumes = "application/x-www-form-urlencoded")
    AppleTokenResponseDto findAppleToken(@RequestBody AppleTokenRequestDto request);

    @PostMapping(value = "/revoke", consumes = "application/x-www-form-urlencoded")
    void revoke(AppleRevokeRequest request);
}
