package com.unit_wiseb.value_calculator.domain.common.filter;

import com.unit_wiseb.value_calculator.domain.user.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * HTTP 요청의 Authorization 헤더에서 JWT 토큰을 추출하고 검증
 * 유효한 토큰인 경우 SecurityContext에 인증 정보 저장
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    /**
     * 요청마다 실행되는 필터 로직
     * Authorization 헤더에서 JWT 토큰 추출 및 검증
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            //Authorization 헤더에서 JWT 토큰 추출
            String token = extractTokenFromRequest(request);

            // 토큰이 있고 유효한 경우
            if (token != null && jwtService.validateToken(token)) {
                //토큰에서 사용자 ID 추출해서
                Long userId = jwtService.getUserIdFromToken(token);

                // spring Security 인증 객체 생성함
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId,  // principal (사용자 ID)
                                null,    // credentials (비밀번호 - JWT는 불필요)
                                new ArrayList<>()  // authorities (권한 목록)
                        );

                //요청 정보 추가
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                //SecurityContext에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("JWT 인증 성공: userId={}", userId);
            }

        } catch (Exception e) {
            log.error("JWT 인증 처리 중 오류 발생: {}", e.getMessage());
            // 인증 실패 시 SecurityContext를 비워둠 (인증되지 않은 상태)
            SecurityContextHolder.clearContext();
        }

        //다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청에서 JWT 토큰 추출
     * Authorization 헤더: "Bearer {token}" 형식
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // "Bearer " 접두사가 있는지 확인
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // "Bearer " 이후의 토큰 부분만 추출
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/api/auth/");
    }



}