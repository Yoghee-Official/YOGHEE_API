package com.lagavulin.yoghee.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor disablePathVariableEncodingInterceptor() {
        return template -> {
            // PathVariable 인코딩 방지
            template.uri(template.path().replaceAll("%3A", ":"));
        };
    }
}