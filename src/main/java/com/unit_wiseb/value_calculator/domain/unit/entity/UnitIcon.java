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

    @Column(name = "icon_path", nullable = false, length = 255)
    private String iconPath;

    @Column(name = "icon_name", nullable = false, length = 50)
    private String iconName;
}