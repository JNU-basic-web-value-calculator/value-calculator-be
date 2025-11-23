package com.unit_wiseb.value_calculator.domain.user.service;

import com.unit_wiseb.value_calculator.domain.common.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    /**
     * 액세스 토큰 생성
     * API 요청 시 인증에 사용(짧은 시간)
     */
    public String generateAccessToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .subject(userId.toString()) // 사용자 ID 저장
                .issuedAt(now) // 발급 시각
                .expiration(expiryDate) // 만료 시각
                .signWith(getSigningKey()) // 서명
                .compact();
    }

    /**
     * 리프레시 토큰 생성
     * 액세스 토큰 갱신에 사용(긴 시간)
     */
    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getRefreshTokenExpiration());

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * JWT 토큰에서 사용자 ID 추출
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }

    /**
     * JWT 토큰 유효성 검증
     * 서명 검증 및 만료 여부 확인
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * JWT 서명에 사용할 SecretKey 생성
     * application.yml의 jwt.secret 값 사용
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 리프레시 토큰 만료 시간 조회 (밀리초)
     */
    public Long getRefreshTokenExpiration() {
        return jwtProperties.getRefreshTokenExpiration();
    }
}