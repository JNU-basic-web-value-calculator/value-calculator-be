package com.unit_wiseb.value_calculator.domain.saving.controller;

import com.unit_wiseb.value_calculator.domain.common.annotation.CurrentUserId;
import com.unit_wiseb.value_calculator.domain.saving.dto.SavingCreateRequest;
import com.unit_wiseb.value_calculator.domain.saving.dto.SavingResponse;
import com.unit_wiseb.value_calculator.domain.saving.dto.SavingStatisticsResponse;
import com.unit_wiseb.value_calculator.domain.saving.service.SavingService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/savings")
@RequiredArgsConstructor
@Tag(name = "Saving", description = "절약 기록 API")
@SecurityRequirement(name = "bearerAuth")
public class SavingController {

    private final SavingService savingService;

    /**
     * 절약 기록 추가
     * POST /api/savings
     */
    @PostMapping
    @Operation(summary = "절약 기록 추가", description = "위시리스트에 절약 금액을 기록합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "절약 기록 추가 성공",
                    content = @Content(schema = @Schema(implementation = SavingResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "위시리스트를 찾을 수 없음")
    })
    public ResponseEntity<SavingResponse> addSaving(
            @Parameter(hidden = true) @CurrentUserId Long userId,
            @Valid @RequestBody SavingCreateRequest request
    ) {
        SavingResponse response = savingService.addSaving(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 절약 통계 조회
     * GET /api/savings/statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "절약 통계 조회", description = "총 절약 금액을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = SavingStatisticsResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<SavingStatisticsResponse> getSavingStatistics(
            @Parameter(hidden = true) @CurrentUserId Long userId
    ) {
        SavingStatisticsResponse response = savingService.getSavingStatistics(userId);
        return ResponseEntity.ok(response);
    }
}
