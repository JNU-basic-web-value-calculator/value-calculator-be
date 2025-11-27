package com.unit_wiseb.value_calculator.domain.common.exception;

import com.unit_wiseb.value_calculator.domain.common.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * IllegalArgumentException 처리
     * - 잘못된 요청 파라미터
     * - 존재하지 않는 리소스 조회
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of("INVALID_INPUT", e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * IllegalStateException 처리
     * - 잘못된 상태에서의 작업 시도
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        log.warn("IllegalStateException: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of("INVALID_STATE", e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * MethodArgumentNotValidException 처리
     * - @Valid 검증 실패 (@NotNull, @Min, @Max 같은거에서)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        log.warn("Validation failed: {}", e.getMessage());

        // 첫 번째 에러 메시지 사용
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = ErrorResponse.of("VALIDATION_FAILED", errorMessage);
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
