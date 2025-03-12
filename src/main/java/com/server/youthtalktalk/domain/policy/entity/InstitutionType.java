package com.server.youthtalktalk.domain.policy.entity;

import static com.server.youthtalktalk.global.response.BaseResponseCode.*;

import com.server.youthtalktalk.global.response.exception.InvalidValueException;
import lombok.Getter;

@Getter
public enum InstitutionType {
    CENTER, LOCAL;

    public static InstitutionType fromString(String value) {
        InstitutionType institutionType = null;
        if (value != null) {
            try {
                institutionType = InstitutionType.valueOf(value.trim().replaceAll("\\s+", "").toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidValueException(INVALID_INPUT_VALUE);
            }
        }
        return institutionType;
    }
}
