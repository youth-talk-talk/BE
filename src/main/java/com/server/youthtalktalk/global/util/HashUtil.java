package com.server.youthtalktalk.global.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashUtil {

    public String hash(String input) {
        try {
            // MessageDigest 인스턴스를 SHA-256 알고리즘으로 초기화
            MessageDigest instance = MessageDigest.getInstance("SHA-256");

            // 입력 문자열을 바이트 배열로 변환 후 해싱하여 바이트 배열로 반환
            byte[] hashBytes = instance.digest(input.getBytes());

            // 해시 결과(바이트 배열)를 16진수 문자열로 변환하여 반환하기 위한 StringBuilder
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                // 각 바이트 값을 2자리 16진수로 변환하여 StringBuilder에 추가
                sb.append(String.format("%02x", b));
            }
            // 최종적으로 해시된 문자열을 반환
            return sb.toString().toUpperCase();

        } catch (NoSuchAlgorithmException e) {
            // SHA-256 알고리즘이 지원되지 않는 경우 예외 처리
            throw new RuntimeException("SHA-256 Algorithm not found", e);
        }
    }

}
