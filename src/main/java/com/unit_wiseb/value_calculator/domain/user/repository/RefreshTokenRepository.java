package com.unit_wiseb.value_calculator.domain.user.repository;

import com.unit_wiseb.value_calculator.domain.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUserId(Long userId);

    //리프레시 토큰 문자열로 조회 -토큰 갱신 시 유효성 검증에 사용
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    //로그아웃때 사용
    void deleteByUserId(Long userId);
}