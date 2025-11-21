package com.unit_wiseb.value_calculator.domain.user.repository;

import com.unit_wiseb.value_calculator.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByKakaoId(Long kakaoId);

    boolean existsByKakaoId(Long kakaoId);
}