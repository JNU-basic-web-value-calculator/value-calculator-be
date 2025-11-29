package com.unit_wiseb.value_calculator.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TokenResponse {

    private String accessToken; //JWT 액세스 토큰 : API 요청 시 인증에 (짧은 시간)
    private String refreshToken;    //액세스 토큰 갱신에 사용(긴 시간)
    private Long userId;
    private String nickname;
}