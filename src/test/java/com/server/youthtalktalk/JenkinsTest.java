package com.server.youthtalktalk;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JenkinsTest {

    @Test
    @DisplayName("테스트1")
    void pipelineTest1() {
        System.out.println("Hello Unit Test");
    }

    @Test
    @DisplayName("테스트2")
    void pipelineTest2() {
        System.out.println("Hello Unit Test2");
    }
}
