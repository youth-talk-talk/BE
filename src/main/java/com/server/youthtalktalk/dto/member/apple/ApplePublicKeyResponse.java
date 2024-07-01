package com.server.youthtalktalk.dto.member.apple;

import lombok.Builder;

import java.util.List;

@Builder
public record ApplePublicKeyResponse(List<AppleKeyInfo> keys) {
}
