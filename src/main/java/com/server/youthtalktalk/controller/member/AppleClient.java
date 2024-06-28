package com.server.youthtalktalk.controller.member;

import com.server.youthtalktalk.config.FeignConfig;
import com.server.youthtalktalk.dto.member.apple.AppleDto;
import com.server.youthtalktalk.dto.member.apple.ApplePublicKeyResponse;
import com.server.youthtalktalk.dto.member.apple.AppleTokenResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.server.youthtalktalk.dto.member.apple.AppleDto.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@FeignClient(name = "appleClient", url = "https://appleid.apple.com/auth", configuration = FeignConfig.class)
public interface AppleClient {
    @GetMapping(value = "/keys")
    ApplePublicKeyResponse findAppleAuthPublicKeys();

    @PostMapping(value = "/token", consumes = APPLICATION_FORM_URLENCODED_VALUE)
    AppleTokenResponseDto findAppleToken(@RequestBody AppleTokenRequestDto request);

    @PostMapping(value = "/revoke", consumes = "application/x-www-form-urlencoded")
    void revoke(AppleRevokeRequest request);
}
