package com.unit_wiseb.value_calculator.domain.calculation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "가치 계산 요청")
public class CalculationRequest {

    @NotNull(message = "계산할 금액은 필수입니다.")
    @Min(value = 1, message = "금액은 1원 이상이어야 합니다.")
    @Schema(description = "계산할 금액(원)", example = "100000")
    private Integer amount;

    @NotNull(message = "환산 단위 ID는 필수입니다.")
    @Schema(description = "환산 단위 ID", example = "1")
    private Long unitId;
}