package com.unit_wiseb.value_calculator.domain.unit.dto;

import com.unit_wiseb.value_calculator.domain.unit.entity.UnitIcon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UnitIconResponse {

    private Long iconId;
    private String iconEmoji;
    private String iconName;

    public static UnitIconResponse from(UnitIcon icon) {
        return UnitIconResponse.builder()
                .iconId(icon.getId())
                .iconEmoji(icon.getIconEmoji())
                .iconName(icon.getIconName())
                .build();
    }
}
