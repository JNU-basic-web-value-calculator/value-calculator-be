package com.unit_wiseb.value_calculator.domain.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 카카오 API 설정 정보
 * kakao.* 속성 자동 매핑
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kakao")
public class KakaoProperties {

    // 카카오 REST API 키
    private String clientId;

    //인가 코드를 받을 Redirect URI
    private String redirectUri;

    //카카오 인증 서버 URL
    private String authUrl;

    //카카오 API 서버 URL
    private String apiUrl;
}
