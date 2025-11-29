package com.unit_wiseb.value_calculator.domain.unit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
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
    @Schema(description = "아이콘 ID", example = "1")
    private Long iconId;

    @NotBlank(message = "단위명은 필수입니다.")
    @Size(max = 50, message = "단위명은 50자 이하")
    @Schema(description = "단위명", example = "스타벅스 라떼")
    private String unitName;

    //TODO 단위 가격은 프론트 구현 후 픽스될 예정. 그때 다시 수정하기
    @NotNull(message = "단위 가격은 필수입니다.")
    @Min(value = 100, message = "단위 가격은 100원 이상")
    @Max(value = 1000000, message = "단위 가격은 1,000,000원 이하")
    @Schema(description = "단위 가격(원)", example = "5500", minimum = "100", maximum = "1000000")
    private Integer unitPrice;

    @NotBlank(message = "수량 단위는 필수입니다.")
    @Size(max = 10, message = "수량 단위는 10자 이하")
    @Schema(description = "수량 단위", example = "잔")
    private String unitCounter;
}
