package com.server.youthtalktalk.domain.member.dto.apple;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AppleKeyInfo {
    private String kty;
    private String kid;
    private String use;
    private String alg;
    private String n;
    private String e;
}
