package com.unit_wiseb.value_calculator.domain.unit.dto;

import com.unit_wiseb.value_calculator.domain.unit.entity.Unit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 단위 정보를 클라이언트에게 반환
 */
@Getter
@Builder
@AllArgsConstructor
public class UnitResponse {

    private Long unitId;

    private String unitName;

    private Integer unitPrice;

    private String unitCounter;

    private String iconPath;

    private String iconName;

    private Boolean isDefault;

    public static UnitResponse from(Unit unit) {
        return UnitResponse.builder()
                .unitId(unit.getId())
                .unitName(unit.getUnitName())
                .unitPrice(unit.getUnitPrice())
                .unitCounter(unit.getUnitCounter())
                .iconPath(unit.getIcon() != null ? unit.getIcon().getIconPath() : null)
                .iconName(unit.getIcon() != null ? unit.getIcon().getIconName() : null)
                .isDefault(unit.getIsDefault())
                .build();
    }
}