package com.unit_wiseb.value_calculator.domain.saving.dto;

import com.unit_wiseb.value_calculator.domain.saving.entity.Saving;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "절약 기록 응답")
public class SavingResponse {

    @Schema(description = "절약 기록 ID", example = "1")
    private Long savingId;

    @Schema(description = "위시리스트 ID", example = "1")
    private Long wishlistId;

    @Schema(description = "절약 금액(원)", example = "5500")
    private Integer amount;

    @Schema(description = "절약 시각", example = "2025-11-24T14:30:00")
    private LocalDateTime savedAt;

    public static SavingResponse from(Saving saving) {
        return SavingResponse.builder()
                .savingId(saving.getSavingId())
                .wishlistId(saving.getWishlistId())
                .amount(saving.getAmount())
                .savedAt(saving.getSavedAt())
                .build();
    }
}