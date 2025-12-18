package com.unit_wiseb.value_calculator.domain.common.config;

import com.unit_wiseb.value_calculator.domain.common.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS 설정 활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // CSRF 보호 비활성화 (JWT 사용 시 불필요)
                .csrf(csrf -> csrf.disable())

                // 세션 사용 안 함 (JWT 기반 인증)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 요청 URL별 인증 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/user/**", "/api/users/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/", "/health").permitAll()
                        .requestMatchers("/api/units/default").permitAll()
                        .requestMatchers("/api/units/icons").permitAll()
                        .anyRequest().authenticated()
                )

                // JWT 인증 필터
                // UsernamePasswordAuthenticationFilter 이전에 실행됨
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * CORS 설정 수정했음
     * - 프론트엔드 로컬 개발 환경에서 백엔드 API 호출 허용
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 출처 (Live Server 기본 포트)
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5500",      // VSCode Live Server 기본 포트
                "http://127.0.0.1:5500",
                "http://localhost:5501",      // 다른 Live Server 인스턴스
                "http://127.0.0.1:5501"
                // 프론트엔드 배포 URL도 여기에 추가 가능
                // "https://your-frontend-domain.com"
        ));

        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // 허용할 헤더
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // 인증 정보 포함 허용 (쿠키, Authorization 헤더 등)
        configuration.setAllowCredentials(true);

        // preflight 요청 캐시 시간 (초)
        configuration.setMaxAge(3600L);

        // 모든 경로에 대해 CORS 설정 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}