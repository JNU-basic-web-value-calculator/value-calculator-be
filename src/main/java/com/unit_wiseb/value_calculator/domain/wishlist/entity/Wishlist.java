package com.unit_wiseb.value_calculator.domain.wishlist.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "wishlists")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_id")
    private Long wishlistId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "item_name", nullable = false, length = 200)
    private String itemName;

    @Column(name = "target_price", nullable = false)
    private Integer targetPrice;

    @Column(name = "current_amount", nullable = false)
    @Builder.Default
    private Integer currentAmount = 0;

    @Column(name = "item_url", length = 500)
    private String itemUrl;

    @Column(name = "is_completed", nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 위시리스트 정보 수정
     * @param itemName 상품명 (null이면 수정 안 함)
     * @param targetPrice 목표 금액 (null이면 수정 안 함)
     * @param itemUrl 상품 URL (null이면 수정 안 함)
     */
    public void updateWishlist(String itemName, Integer targetPrice, String itemUrl) {
        if (itemName != null) {
            this.itemName = itemName;
        }
        if (targetPrice != null) {
            this.targetPrice = targetPrice;
        }
        if (itemUrl != null) {
            this.itemUrl = itemUrl;
        }
    }

    /**
     * 절약 금액 추가 (절약 기록 생성 시 호출)
     * 목표 금액 달성 시 isCompleted = true로 변경
     */
    public void addSavingAmount(Integer amount) {
        this.currentAmount += amount;

        // 목표 달성 체크
        if (this.currentAmount >= this.targetPrice && !this.isCompleted) {
            this.isCompleted = true;
            this.completedAt = LocalDateTime.now();
        }
    }

    /**
     * 절약 금액 차감 (절약 기록 삭제 시 호출)
     * 목표 금액 미달 시 isCompleted = false로 변경
     */
    public void subtractSavingAmount(Integer amount) {
        this.currentAmount -= amount;

        // 목표 미달성으로 변경
        if (this.currentAmount < this.targetPrice && this.isCompleted) {
            this.isCompleted = false;
            this.completedAt = null;
        }
    }

    /**
     * 목표 달성률 계산(0.0 ~ 100.0)
     */
    public double getCompletionRate() {
        if (targetPrice == 0) {
            return 0.0;
        }
        return Math.min((double) currentAmount / targetPrice * 100, 100.0);
    }
}