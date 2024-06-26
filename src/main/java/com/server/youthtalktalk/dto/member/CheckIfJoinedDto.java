package com.server.youthtalktalk.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CheckIfJoinedDto {
    String socialType;
    String socialId;
}
