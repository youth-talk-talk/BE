package com.server.youthtalktalk;

import org.apache.catalina.security.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;

@SpringBootApplication
public class YouthTalkTalkApplication {

	public static void main(String[] args) {
		SpringApplication.run(YouthTalkTalkApplication.class, args);
	}

}
