package com.unit_wiseb.value_calculator.domain.user.controller;

import com.unit_wiseb.value_calculator.domain.common.config.KakaoProperties;
import com.unit_wiseb.value_calculator.domain.user.dto.TokenResponse;
import com.unit_wiseb.value_calculator.domain.user.service.KakaoAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Tag(name = "인증 API", description = "카카오 소셜 로그인 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KakaoAuthService kakaoAuthService;
    private final KakaoProperties kakaoProperties;

    /**
     * 카카오 로그인 페이지로 리다이렉트하는 URL 생성
     * GET /api/auth/kakao/login
     * 클라이언트가 이 URL로 요청하면 카카오 로그인 URL을 반환
     * 실제 리다이렉트는 클라이언트에서
     */
    @Operation(
            summary = "카카오 로그인 URL 조회",
            description = "카카오 로그인 페이지 URL을 반환합니다. 이 URL로 리다이렉트하여 카카오 로그인을 시작하세요."
    )
    @ApiResponse(responseCode = "200", description = "카카오 로그인 URL 반환 성공")
    @GetMapping("/kakao/login")
    public ResponseEntity<String> kakaoLoginPage() {
        // 카카오 로그인 URL 생성
        String kakaoAuthUrl = kakaoProperties.getAuthUrl() + "/oauth/authorize"
                + "?client_id=" + kakaoProperties.getClientId()
                + "&redirect_uri=" + URLEncoder.encode(kakaoProperties.getRedirectUri(), StandardCharsets.UTF_8)
                + "&response_type=code";

        log.info("카카오 로그인 URL 생성: {}", kakaoAuthUrl);

        return ResponseEntity.ok(kakaoAuthUrl);
    }

    /**
     * 카카오 로그인 콜백 처리
     * GET /api/auth/kakao/callback?code={인가코드}
     * 카카오 로그인 후 리다이렉트되는 엔드포인트
     * 인가 코드 -> 로그인 처리 -> JWT 토큰 반환
     */
    @Operation(
            summary = "카카오 로그인 콜백",
            description = "카카오 인가 코드를 받아 로그인 처리 후 JWT 토큰을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 인가 코드"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/kakao/callback")
    public ResponseEntity<TokenResponse> kakaoCallback(
            @Parameter(description = "카카오 인가 코드", required = true)
            @RequestParam String code
    ) {
        log.info("카카오 콜백 요청 수신: code={}", code);

        // 카카오 로그인 처리 (인가 코드 → JWT 토큰)
        TokenResponse tokenResponse = kakaoAuthService.login(code);

        log.info("카카오 로그인 성공: userId={}, nickname={}",
                tokenResponse.getUserId(), tokenResponse.getNickname());

        return ResponseEntity.ok(tokenResponse);
    }

    /**
     * 로그아웃
     * POST /api/auth/logout
     * 리프레시 토큰을 DB에서 삭제
     * 클라이언트는 로컬 스토리지의 토큰도 함께 삭제해야 함!!!!
     */
    @Operation(
            summary = "로그아웃",
            description = "리프레시 토큰을 삭제하여 로그아웃 처리합니다."
    )
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId
    ) {
        log.info("로그아웃 요청: userId={}", userId);

        kakaoAuthService.logout(userId);

        return ResponseEntity.ok("로그아웃 성공");
    }

    /**
     * 토큰 갱신
     * POST /api/auth/refresh
     * 리프레시 토큰으로 새로운 액세스 토큰 발급
     * (추후 구현 예정)
     */
    @Operation(
            summary = "토큰 갱신 (미구현)",
            description = "리프레시 토큰으로 새로운 액세스 토큰을 발급합니다. (추후 구현 예정)"
    )
    @ApiResponse(responseCode = "200", description = "토큰 갱신 성공")
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(
            @Parameter(description = "리프레시 토큰", required = true)
            @RequestBody String refreshToken
    ) {
        // TODO: 리프레시 토큰 검증 및 액세스 토큰 재발급
        log.info("토큰 갱신 요청 (아직...미구현)");
        return ResponseEntity.ok("토큰 갱신 기능 추후 구현 예정");
    }
}