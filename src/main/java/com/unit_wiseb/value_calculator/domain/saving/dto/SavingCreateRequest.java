package com.unit_wiseb.value_calculator.domain.saving.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "절약 기록 생성 요청")
public class SavingCreateRequest {

    @NotNull(message = "위시리스트 ID는 필수입니다.")
    @Schema(description = "위시리스트 ID", example = "1")
    private Long wishlistId;

    @NotNull(message = "절약 금액은 필수입니다.")
    @Min(value = 1, message = "절약 금액은 1원 이상이어야 합니다.")
    @Schema(description = "절약 금액(원)", example = "5500")
    private Integer amount;
}