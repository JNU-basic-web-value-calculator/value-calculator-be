package com.unit_wiseb.value_calculator.domain.saving.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "절약 통계 응답")
public class SavingStatisticsResponse {

    @Schema(description = "총 절약 금액(원)", example = "82500")
    private Integer totalAmount;

    /**
     * 총 절약 금액으로 응답 객체 생성
     */
    public static SavingStatisticsResponse of(Integer totalAmount) {
        return SavingStatisticsResponse.builder()
                .totalAmount(totalAmount != null ? totalAmount : 0)
                .build();
    }

}