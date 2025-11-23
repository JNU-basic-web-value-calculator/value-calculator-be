package com.unit_wiseb.value_calculator.domain.unit.entity;

import com.unit_wiseb.value_calculator.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "units")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_id")
    private Long id;

    /**
     * 사용자 ID
     * NULL: 기본 단위
     * NOT NULL: 특정 사용자의 커스텀 단위
     */
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "icon_id", nullable = false)
    private Long iconId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icon_id", insertable = false, updatable = false)
    private UnitIcon icon;

    @Column(name = "unit_name", nullable = false, length = 50)
    private String unitName;

    @Column(name = "unit_price", nullable = false)
    private Integer unitPrice;

    /**
     * 수량 단위 (잔, 번, 개, 회? 같은)
     */
    @Column(name = "unit_counter", nullable = false, length = 10)
    private String unitCounter;

    /**
     * 기본 단위 여부
     * TRUE: 앱에서 제공하는 기본 단위
     * FALSE: 사용자가 만든 커스텀 단위
     */
    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    /**
     * 커스텀 단위 정보 업데이트
     */
    public void updateCustomUnit(String unitName, Integer unitPrice, String unitCounter, Long iconId) {
        if (this.isDefault) {
            throw new IllegalStateException("기본 단위는 수정 불가");
        }
        this.unitName = unitName;
        this.unitPrice = unitPrice;
        this.unitCounter = unitCounter;
        this.iconId = iconId;
    }
}