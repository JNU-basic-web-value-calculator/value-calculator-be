package com.unit_wiseb.value_calculator.domain.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
//카카오 서버와 통신하는 도구인 webclient를 이 config 클래스로 spring에 등록해서 어디서든 사용할 수 있게