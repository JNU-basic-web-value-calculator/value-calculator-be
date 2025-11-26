package com.unit_wiseb.value_calculator.domain.calculation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "가치 계산 응답")
public class CalculationResponse {

    @Schema(description = "입력 금액(원)", example = "100000")
    private Integer amount;

    @Schema(description = "환산 단위 ID", example = "1")
    private Long unitId;

    @Schema(description = "환산 단위명", example = "아메리카노")
    private String unitName;

    @Schema(description = "환산 단위 가격(원)", example = "4500")
    private Integer unitPrice;

    @Schema(description = "환산 단위 수량 단위", example = "잔")
    private String unitCounter;

    @Schema(description = "환산 결과 (소수점 1자리)", example = "22.2")
    private Double result;

    @Schema(description = "아이콘 이모지", example = "☕")
    private String iconEmoji;

    @Schema(description = "아이콘 이름", example = "커피")
    private String iconName;
}