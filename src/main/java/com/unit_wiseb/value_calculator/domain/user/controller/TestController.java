package com.unit_wiseb.value_calculator.domain.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// 서버 상태 확인용
@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "서버 정상 작동 확인";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}