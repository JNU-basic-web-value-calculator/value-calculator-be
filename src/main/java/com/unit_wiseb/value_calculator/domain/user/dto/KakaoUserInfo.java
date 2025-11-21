package com.unit_wiseb.value_calculator.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfo {

    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {
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
        if (kakaoAccount != null && kakaoAccount.getProfile() != null) {
            return kakaoAccount.getProfile().getNickname();
        }
        return null;
    }
}