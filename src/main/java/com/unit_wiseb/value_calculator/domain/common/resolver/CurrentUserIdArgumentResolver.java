package com.unit_wiseb.value_calculator.domain.common.resolver;

import com.unit_wiseb.value_calculator.domain.common.annotation.CurrentUserId;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @CurrentUserId 어노테이션 처리 Resolver
 * SecurityContext에서 인증된 사용자 ID를 추출하여 파라미터에 주입
 */
@Component
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 이 Resolver가 처리할 수 있는 파라미터인지 확인
     * -> @CurrentUserId 어노테이션이 있는 Long 타입 파라미터만 처리
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class)
                && Long.class.equals(parameter.getParameterType());
    }

    /**
     * 파라미터 값 추출
     * SecurityContext에서 인증된 사용자 ID를 가져옴
     */
    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증되지 않았으면,
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }

        // principal에서 (JwtAuthenticationFilter에서 저장한 값)userId 추출
        Object principal = authentication.getPrincipal();

        if (principal instanceof Long) {
            return principal;
        }

        throw new IllegalStateException("유효하지 않은 인증 정보입니다.");
    }
}