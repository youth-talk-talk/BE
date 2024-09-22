package com.server.youthtalktalk.global.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class HashUtilTest {

    @Autowired
    private HashUtil hashUtil;

    /**
     * SHA-256 알고리즘으로 올바른 해시값을 반환하는지 검증
     */
    @Test
    public void SHA256_알고리즘으로_해시값을_반환() throws Exception {
        // given
        String input = "test";
        String expectedHash = "9F86D081884C7D659A2FEAA0C55AD015A3BF4F1B2B0B822CD15D6C15B0F00A08";

        // when
        String result = hashUtil.hash(input);

        // then
        assertThat(result).isEqualTo(expectedHash);

    }

    /**
     * 동일한 input에 대하여 hash() 메서드가 동일한 해시값을 반환하는지 검증
     */
    @Test
    public void 같은_입력값이면_같은_해시값_반환() throws Exception {
        // given
        String input = "plainText";
        String hashed = hashUtil.hash(input);

        // when
        String result1 = hashUtil.hash(input);
        String result2 = hashUtil.hash(input);

        // then
        assertThat(result1).isEqualTo(hashed);
        assertThat(result2).isEqualTo(hashed);
    }

    /**
     * 서로 다른 input에 대하여 hash() 메서드가 서로 다른 해시값 반환하는지 검증
     */
    @Test
    public void 다른_입력값이면_다른_해시값_반환() throws Exception {
        // given
        String input1 = "test1";
        String input2 = "test2";

        // when
        String hashed1 = hashUtil.hash(input1);
        String hashed2 = hashUtil.hash(input2);

        // then
        assertThat(hashed1).isNotEqualTo(hashed2);
    }

}