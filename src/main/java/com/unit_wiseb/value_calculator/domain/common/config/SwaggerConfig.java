package com.unit_wiseb.value_calculator.domain.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("가치 계산기 API")
                        .description("소비 인식 개선을 위한 가치 환산 서비스 백엔드 API 문서입니다!")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("unit_WISE-B")
                        )
                )
                // JWT 인증 설정
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                                .description("JWT 액세스 토큰을 입력하세요.\n\n" +
                                        "1. /api/auth/kakao/login으로 카카오 로그인\n" +
                                        "2. /api/auth/kakao/callback에서 받은 accessToken 복사\n" +
                                        "3. 우측 상단 [Authorize] 버튼 클릭\n" +
                                        "4. 'Bearer ' 없이 토큰만 입력\n\n")
                        )
                )
                // 전역 보안 요구사항
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearer-jwt")
                );
    }
}