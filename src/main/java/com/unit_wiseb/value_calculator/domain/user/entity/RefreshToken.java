package com.unit_wiseb.value_calculator.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshToken {
    /**
     * JWT 리프레시 토큰을 DB에 저장하기 위함
     * 사용자당 1개의 리프레시 토큰 유지 -> UNIQUE
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "refresh_token", nullable = false, unique = true, length = 500)
    private String refreshToken;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    //엔티티가 처음 저장될 때 자동으로 실행
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    //새 로그인 시 새 토큰으로 교체
    public void updateToken(String newToken, LocalDateTime newExpiresAt) {
        this.refreshToken = newToken;
        this.expiresAt = newExpiresAt;
    }
}