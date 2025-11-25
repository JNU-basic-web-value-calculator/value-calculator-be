package com.unit_wiseb.value_calculator.domain.wishlist.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "위시리스트 생성 요청")
public class WishlistCreateRequest {

    @NotBlank(message = "상품명은 필수입니다.")
    @Size(max = 200, message = "상품명은 200자 이하여야 합니다.(추후 정할 것)")
    @Schema(description = "상품명", example = "에어팟 프로 2세대")
    private String itemName;

    @NotNull(message = "목표 금액은 필수입니다.")
    @Min(value = 1, message = "목표 금액은 1원 이상이어야 합니다.")
    @Schema(description = "목표 금액(원)", example = "700000")
    private Integer targetPrice;

    @Size(max = 500, message = "상품 URL은 500자 이하여야 합니다.")
    @Schema(description = "상품 URL (선택)", example = "https://www.apple.com/kr/airpods-pro/")
    private String itemUrl;
}