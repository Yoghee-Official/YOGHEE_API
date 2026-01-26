package com.lagavulin.yoghee.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 로컬 개발 환경 전용 Web MVC 설정 local 프로파일에서만 활성화됨
 */
@Configuration
@Profile("local")
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // /self.html 또는 /self 로 요청 들어오면 templates/self.html 을 렌더링
        registry.addViewController("/self.html").setViewName("self");
        registry.addViewController("/self").setViewName("self");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 정적 리소스 핸들러 명시적 추가
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCachePeriod(3600); // 1시간 캐시

        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        // 기본 정적 리소스 설정 유지
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}


