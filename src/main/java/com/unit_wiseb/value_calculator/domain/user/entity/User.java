package com.unit_wiseb.value_calculator.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "kakao_id", nullable = false, unique = true)
    private Long kakaoId;

    @Column(name = "nickname", length = 100)
    private String nickname;

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}