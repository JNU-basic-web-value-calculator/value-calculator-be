package com.unit_wiseb.value_calculator.domain.calculation.controller;

import com.unit_wiseb.value_calculator.domain.calculation.dto.CalculationRequest;
import com.unit_wiseb.value_calculator.domain.calculation.dto.CalculationResponse;
import com.unit_wiseb.value_calculator.domain.calculation.service.CalculationService;
import com.unit_wiseb.value_calculator.domain.common.annotation.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calculate")
@RequiredArgsConstructor
@Tag(name = "Calculation", description = "가치 계산 API")
@SecurityRequirement(name = "bearerAuth")
public class CalculationController {

    private final CalculationService calculationService;

    /**
     * 가치 계산
     * POST /api/calculate
     */
    @PostMapping
    @Operation(summary = "가치 계산", description = "입력된 금액을 환산 단위로 변환합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "계산 성공",
                    content = @Content(schema = @Schema(implementation = CalculationResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "환산 단위를 찾을 수 없음")
    })
    public ResponseEntity<CalculationResponse> calculate(
            @Parameter(hidden = true) @CurrentUserId Long userId,
            @Valid @RequestBody CalculationRequest request
    ) {
        CalculationResponse response = calculationService.calculate(userId, request);
        return ResponseEntity.ok(response);
    }
}