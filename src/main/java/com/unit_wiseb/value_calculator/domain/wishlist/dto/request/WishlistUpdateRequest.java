package com.unit_wiseb.value_calculator.domain.wishlist.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 모든 필드는 선택사항이며, 제공된 필드만 수정되도록함
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "위시리스트 수정 요청")
public class WishlistUpdateRequest {

    @Size(max = 200, message = "상품명은 200자 이하여야 합니다.(추후 정할 것)")
    @Schema(description = "상품명 (선택)", example = "에어팟 프로 3세대")
    private String itemName;

    @Min(value = 1, message = "목표 금액은 1원 이상이어야 합니다.")
    @Schema(description = "목표 금액(원) (선택)", example = "700000")
    private Integer targetPrice;

    @Size(max = 500, message = "상품 URL은 500자 이하여야 합니다.")
    @Schema(description = "상품 URL (선택)", example = "https://www.apple.com/kr/airpods-pro/")
    private String itemUrl;
}