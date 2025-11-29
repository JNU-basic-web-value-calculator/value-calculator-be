package com.unit_wiseb.value_calculator.domain.unit.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "unit_icons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UnitIcon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "icon_id")
    private Long id;

    @Column(name = "icon_emoji", nullable = false, length = 10)
    private String iconEmoji;

    @Column(name = "icon_name", nullable = false, length = 50)
    private String iconName;
}