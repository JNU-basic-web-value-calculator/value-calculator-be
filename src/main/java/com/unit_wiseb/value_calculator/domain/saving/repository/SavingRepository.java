package com.unit_wiseb.value_calculator.domain.saving.repository;

import com.unit_wiseb.value_calculator.domain.saving.entity.Saving;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 절약 기록 Repository
 */
@Repository
public interface SavingRepository extends JpaRepository<Saving, Long> {

    /**
     * 특정 사용자의 총 절약 금액 계산
     * @param userId 사용자 ID
     * @return 총 절약 금액
     */
    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM Saving s WHERE s.userId = :userId")
    Integer getTotalSavingAmount(@Param("userId") Long userId);
}