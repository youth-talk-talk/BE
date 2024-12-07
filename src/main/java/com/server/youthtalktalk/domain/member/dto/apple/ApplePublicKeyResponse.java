package com.server.youthtalktalk.domain.member.dto.apple;

import lombok.Builder;

import java.util.List;

@Builder
public record ApplePublicKeyResponse(List<AppleKeyInfo> keys) {
}
