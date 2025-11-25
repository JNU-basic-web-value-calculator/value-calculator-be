package com.unit_wiseb.value_calculator.domain.wishlist.dto.response;

import com.unit_wiseb.value_calculator.domain.wishlist.entity.Wishlist;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

//목록용
@Getter
@AllArgsConstructor
@Builder
@Schema(description = "위시리스트 응답")
public class WishlistResponse {

    @Schema(description = "위시리스트 ID", example = "1")
    private Long wishlistId;

    @Schema(description = "상품명", example = "에어팟 프로 2세대")
    private String itemName;

    @Schema(description = "목표 금액(원)", example = "359000")
    private Integer targetPrice;

    @Schema(description = "현재 절약 금액(원)", example = "150000")
    private Integer currentAmount;

    @Schema(description = "상품 URL", example = "https://www.apple.com/kr/airpods-pro/")
    private String itemUrl;

    @Schema(description = "목표 달성 여부", example = "false")
    private Boolean isCompleted;

    @Schema(description = "목표 달성률(%)", example = "41.78")
    private Double completionRate;

    @Schema(description = "목표 달성 시각", example = "2025-11-24T10:30:00")
    private LocalDateTime completedAt;

    @Schema(description = "생성 시각", example = "2025-11-20T14:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시각", example = "2025-11-24T09:15:00")
    private LocalDateTime updatedAt;



    public static WishlistResponse from(Wishlist wishlist) {
        return WishlistResponse.builder()
                .wishlistId(wishlist.getWishlistId())
                .itemName(wishlist.getItemName())
                .targetPrice(wishlist.getTargetPrice())
                .currentAmount(wishlist.getCurrentAmount())
                .itemUrl(wishlist.getItemUrl())
                .isCompleted(wishlist.getIsCompleted())
                .completionRate(wishlist.getCompletionRate())
                .completedAt(wishlist.getCompletedAt())
                .createdAt(wishlist.getCreatedAt())
                .updatedAt(wishlist.getUpdatedAt())
                .build();
    }
}