package com.unit_wiseb.value_calculator.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 액세스 토큰 응답 DTO
 * 토큰 갱신 시 새로운 액세스 토큰만 반환
 */
@Getter
@Builder
@AllArgsConstructor
public class AccessTokenResponse {

    //새로 발급된 JWT 액세스 토큰
    private String accessToken;
    private Long userId;
}