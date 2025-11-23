package com.unit_wiseb.value_calculator.domain.unit.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 커스텀 단위 생성 요청 DTO
 * 각 항목들 전부 필수 요건임.
 */
@Getter
@NoArgsConstructor
public class UnitRequest {

    @NotNull(message = "아이콘 ID는 필수입니다.")
    private Long iconId;

    @NotBlank(message = "단위명은 필수입니다.")
    private String unitName;

    @NotNull(message = "단위 가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Integer unitPrice;

    @NotBlank(message = "수량 단위는 필수입니다.")
    private String unitCounter;
}
