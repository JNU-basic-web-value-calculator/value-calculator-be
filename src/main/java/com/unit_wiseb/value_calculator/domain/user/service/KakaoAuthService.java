package com.unit_wiseb.value_calculator.domain.user.service;

import com.unit_wiseb.value_calculator.domain.common.config.KakaoProperties;
import com.unit_wiseb.value_calculator.domain.user.dto.AccessTokenResponse;
import com.unit_wiseb.value_calculator.domain.user.dto.KakaoUserInfo;
import com.unit_wiseb.value_calculator.domain.user.dto.TokenResponse;
import com.unit_wiseb.value_calculator.domain.user.entity.RefreshToken;
import com.unit_wiseb.value_calculator.domain.user.entity.User;
import com.unit_wiseb.value_calculator.domain.user.repository.RefreshTokenRepository;
import com.unit_wiseb.value_calculator.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 카카오 소셜 로그인 서비스
 * 카카오 OAuth 인증 및 사용자 정보 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final WebClient webClient;
    private final KakaoProperties kakaoProperties;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    /**
     * 카카오 로그인 URL 생성
     *
     * @return 카카오 OAuth 인증 URL
     */
    public String getKakaoLoginUrl() {
        String loginUrl = String.format(
                "%s/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code",
                kakaoProperties.getAuthUrl(),
                kakaoProperties.getClientId(),
                kakaoProperties.getRedirectUri()
        );

        log.info("카카오 로그인 URL 생성: {}", loginUrl);
        return loginUrl;
    }

    /**
     * 카카오 액세스 토큰으로 사용자 정보 조회
     * GET https://kapi.kakao.com/v2/user/me
     *
     * @param accessToken 카카오 액세스 토큰
     * @return 카카오 사용자 정보
     */
    private KakaoUserInfo getKakaoUserInfo(String accessToken) {
        String userInfoUri = kakaoProperties.getApiUrl() + "/v2/user/me";

        // 카카오 서버에 HTTP GET 요청
        return webClient.get()
                .uri(userInfoUri)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserInfo.class)
                .block();
    }

    /**
     * 카카오 로그인 처리
     * 1. 인가 코드로 카카오 액세스 토큰 요청
     * 2. 카카오 액세스 토큰으로 사용자 정보 조회
     * 3. 신규 회원이면 가입, 기존 회원이면 정보 업데이트
     * 4. JWT 토큰(액세스 토큰 + 리프레시 토큰) 생성 및 반환
     */
    @Transactional
    public TokenResponse login(String code) {
        // 인가 코드로 카카오 액세스 토큰 받기
        String kakaoAccessToken = getKakaoAccessToken(code);
        log.info("카카오 액세스 토큰 발급 완료");

        // 카카오 액세스 토큰으로 사용자 정보 가져오기
        KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(kakaoAccessToken);
        log.info("카카오 사용자 정보 조회 완료: kakaoId={}", kakaoUserInfo.getKakaoId());

        // 사용자 정보 저장 또는 업데이트
        User user = saveOrUpdateUser(kakaoUserInfo);

        // JWT 토큰 생성
        String accessToken = jwtService.generateAccessToken(user.getId());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        //리프레시 토큰 저장 (기존 토큰이 있으면 업데이트)
        saveOrUpdateRefreshToken(user.getId(), refreshToken);

        log.info("로그인 완료: userId={}, nickname={}", user.getId(), user.getNickname());

        // 토큰 응답 반환
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .nickname(user.getNickname())
                .build();
    }

    /**
     * 카카오 인가 코드로 액세스 토큰 요청
     * POST https://kauth.kakao.com/oauth/token
     * @param code 카카오 인가 코드
     * @return 카카오 액세스 토큰
     */
    private String getKakaoAccessToken(String code) {
        String tokenUri = kakaoProperties.getAuthUrl() + "/oauth/token";

        //MultiValueMap 사용 (application/x-www-form-urlencoded 형식)
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", kakaoProperties.getClientId());
        formData.add("redirect_uri", kakaoProperties.getRedirectUri());
        formData.add("code", code);

        // 카카오 서버에 HTTP POST 요청
        Map<String, Object> response = webClient.post()
                .uri(tokenUri)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData(formData))  //fromFormData
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return (String) response.get("access_token");
    }

    /**
     * 사용자 정보 저장 또는 업데이트
     * - 신규 회원: DB에 새로 저장
     * - 기존 회원: 닉네임 업데이트
     *
     * @param kakaoUserInfo 카카오 사용자 정보
     * @return 저장/업데이트된 사용자 엔티티
     */
    private User saveOrUpdateUser(KakaoUserInfo kakaoUserInfo) {
        Long kakaoId = kakaoUserInfo.getKakaoId();
        String nickname = kakaoUserInfo.getNickname();

        // 카카오 ID로 기존 회원 조회
        return userRepository.findByKakaoId(kakaoId)
                .map(existingUser -> {
                    // 기존 회원: 닉네임 업데이트
                    existingUser.updateNickname(nickname);
                    log.info("기존 회원 정보 업데이트: userId={}", existingUser.getId());
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    // 신규 회원: 새로 저장
                    User newUser = User.builder()
                            .kakaoId(kakaoId)
                            .nickname(nickname)
                            .build();
                    User savedUser = userRepository.save(newUser);
                    log.info("신규 회원 가입: userId={}", savedUser.getId());
                    return savedUser;
                });
    }

    /**
     * 리프레시 토큰 저장 또는 업데이트
     * - 기존 토큰 있음 -> 새 토큰으로 업데이트
     * - 기존 토큰 없음 -> 새로 저장
     *
     * @param userId 사용자 ID
     * @param refreshToken 리프레시 토큰 값
     */
    private void saveOrUpdateRefreshToken(Long userId, String refreshToken) {
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtService.getRefreshTokenExpiration() / 1000);

        refreshTokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        // 기존 토큰 업데이트
                        existingToken -> {
                            existingToken.updateToken(refreshToken, expiresAt);
                            refreshTokenRepository.save(existingToken);
                            log.info("리프레시 토큰 업데이트: userId={}", userId);
                        },
                        // 새 토큰 저장
                        () -> {
                            RefreshToken newToken = RefreshToken.builder()
                                    .userId(userId)
                                    .refreshToken(refreshToken)
                                    .expiresAt(expiresAt)
                                    .build();
                            refreshTokenRepository.save(newToken);
                            log.info("리프레시 토큰 저장: userId={}", userId);
                        }
                );
    }


    /**
     * 리프레시 토큰으로 액세스 토큰 갱신
     * 1. 리프레시 토큰 검증 (JWT 유효성 + DB 존재 여부)
     * 2. 리프레시 토큰 만료 여부 확인
     * 3. 새로운 액세스 토큰 발급
     */
    @Transactional(readOnly = true)
    public AccessTokenResponse refreshAccessToken(String refreshTokenValue) {
        //리프레시 토큰 JWT 유효성 검증
        if (!jwtService.validateToken(refreshTokenValue)) {
            log.error("유효하지 않은 리프레시 토큰: {}", refreshTokenValue);
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        //DB에서 리프레시 토큰 조회
        RefreshToken refreshToken = refreshTokenRepository
                .findByRefreshToken(refreshTokenValue)
                .orElseThrow(() -> {
                    log.error("DB에 존재하지 않는 리프레시 토큰: {}", refreshTokenValue);
                    return new IllegalArgumentException("존재하지 않는 리프레시 토큰입니다.");
                });

        //리프레시 토큰 만료 여부 확인
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.error("만료된 리프레시 토큰: userId={}", refreshToken.getUserId());
            throw new IllegalArgumentException("만료된 리프레시 토큰입니다. 다시 로그인해주세요.");
        }

        //사용자 존재 여부 확인
        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> {
                    log.error("존재하지 않는 사용자: userId={}", refreshToken.getUserId());
                    return new IllegalArgumentException("존재하지 않는 사용자입니다.");
                });

        //새로운 액세스 토큰 생성
        String newAccessToken = jwtService.generateAccessToken(user.getId());
        log.info("액세스 토큰 갱신 성공: userId={}, nickname={}", user.getId(), user.getNickname());

        return AccessTokenResponse.builder()
                .accessToken(newAccessToken)
                .userId(user.getId())
                .build();
    }





    /**
     * 로그아웃 - 리프레시 토큰 삭제
     */
//    @Transactional
//    public void logout(Long userId) {
//        refreshTokenRepository.deleteByUserId(userId);
//        log.info("로그아웃 완료: userId={}", userId);
//    }
}