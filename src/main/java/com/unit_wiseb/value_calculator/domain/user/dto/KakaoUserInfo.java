package com.unit_wiseb.value_calculator.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 카카오 사요자 정보 응답 DTO
 * 카카오 API에서 받은 사용자 정보 매핑하기 위함임
 */
@Getter
@NoArgsConstructor
public class KakaoUserInfo {

    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    //카카오계정 정보 내부 클래스
    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {
        //비즈앱이 아니여서 닉네임만 받아오도록
        private Profile profile;

        @Getter
        @NoArgsConstructor
        public static class Profile {
            private String nickname;
        }
    }

    public Long getKakaoId() {
        return id;
    }

    public String getNickname() {
        //널 체크하고 접근하도록
        if (kakaoAccount != null && kakaoAccount.getProfile() != null) {
            return kakaoAccount.getProfile().getNickname();
        }
        return null;
    }
}