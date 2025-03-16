package com.server.youthtalktalk.global.config;

import com.server.youthtalktalk.global.log.LoggingFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public FilterRegistrationBean loggingFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LoggingFilter());
        filterRegistrationBean.setOrder(0);
        filterRegistrationBean.addUrlPatterns("/api/*");

        return filterRegistrationBean;
    }
}
