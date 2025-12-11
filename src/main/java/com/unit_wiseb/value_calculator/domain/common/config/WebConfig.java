package com.unit_wiseb.value_calculator.domain.common.config;

import com.unit_wiseb.value_calculator.domain.common.resolver.CurrentUserIdArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CurrentUserIdArgumentResolver currentUserIdArgumentResolver;

    /**
     * 커스텀 ArgumentResolver 등록
     * @CurrentUserId 어노테이션 처리를 위한 Resolver 추가
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserIdArgumentResolver);
    }

    /**
     * Static Resource 경로 설정
     * /uploads/icons/** 요청을 실제 파일 경로로 매핑
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/icons/**")
                .addResourceLocations("classpath:/static/uploads/icons/");
    }
}