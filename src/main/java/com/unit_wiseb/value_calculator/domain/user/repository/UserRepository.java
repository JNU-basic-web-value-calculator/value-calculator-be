package com.unit_wiseb.value_calculator.domain.user.repository;

import com.unit_wiseb.value_calculator.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //카카오 ID로 사용자 조회
    Optional<User> findByKakaoId(Long kakaoId);

    //카카오 ID로 사용자 존재 여부 확인
    boolean existsByKakaoId(Long kakaoId);
}