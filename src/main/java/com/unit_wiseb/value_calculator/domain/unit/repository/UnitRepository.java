package com.unit_wiseb.value_calculator.domain.unit.repository;

import com.unit_wiseb.value_calculator.domain.unit.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    /**
     * 기본 단위 목록 조회
     * user_id가 NULL && is_default가 TRUE
     */
    List<Unit> findByUserIdIsNullAndIsDefaultTrue();

    /**
     * 특정 사용자의 커스텀 단위 목록 조회
     */
    List<Unit> findByUserId(Long userId);

    /**
     * 특정 사용자의 모든 단위 조회 (기본 + 커스텀)
     * 기본 단위 + 해당 사용자의 커스텀 단위
     */
    List<Unit> findByUserIdIsNullAndIsDefaultTrueOrUserId(Long userId);
}