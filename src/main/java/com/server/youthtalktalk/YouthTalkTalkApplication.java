package com.server.youthtalktalk;

import org.apache.catalina.security.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class YouthTalkTalkApplication {

	public static void main(String[] args) {
		SpringApplication.run(YouthTalkTalkApplication.class, args);
	}

}
