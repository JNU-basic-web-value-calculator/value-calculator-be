package com.unit_wiseb.value_calculator.domain.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "에러 응답")
public class ErrorResponse {

    @Schema(description = "에러 코드", example = "INVALID_INPUT")
    private String code;

    @Schema(description = "에러 메시지", example = "잘못된 입력입니다.")
    private String message;

    @Schema(description = "에러 발생 시각", example = "2025-11-24T23:30:00")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * 에러 응답 생성
     * @param code
     * @param message
     * @return ErrorResponse
     */
    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .build();
    }
}