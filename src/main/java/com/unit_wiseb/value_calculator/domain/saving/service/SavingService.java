package com.unit_wiseb.value_calculator.domain.saving.service;

import com.unit_wiseb.value_calculator.domain.saving.dto.SavingCreateRequest;
import com.unit_wiseb.value_calculator.domain.saving.dto.SavingResponse;
import com.unit_wiseb.value_calculator.domain.saving.dto.SavingStatisticsResponse;
import com.unit_wiseb.value_calculator.domain.saving.entity.Saving;
import com.unit_wiseb.value_calculator.domain.saving.repository.SavingRepository;
import com.unit_wiseb.value_calculator.domain.wishlist.entity.Wishlist;
import com.unit_wiseb.value_calculator.domain.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 절약 기록 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SavingService {

    private final SavingRepository savingRepository;
    private final WishlistRepository wishlistRepository;

    /**
     * 절약 기록 추가
     * 위시리스트의 current_amount를 증가시키고 목표 달성 여부를 확인
     */
    @Transactional
    public SavingResponse addSaving(Long userId, SavingCreateRequest request) {
        log.info("절약 기록 추가 요청: userId={}, wishlistId={}, amount={}",
                userId, request.getWishlistId(), request.getAmount());

        // 위시리스트 조회 및 권한 확인
        Wishlist wishlist = wishlistRepository
                .findByWishlistIdAndUserId(request.getWishlistId(), userId)
                .orElseThrow(() -> {
                    log.warn("위시리스트를 찾을 수 없음: wishlistId={}, userId={}",
                            request.getWishlistId(), userId);
                    return new IllegalArgumentException("위시리스트를 찾을 수 없습니다.");
                });

        // 완료된 위시리스트에는 절약 기록 추가 불가
        if (wishlist.getIsCompleted()) {
            log.warn("완료된 위시리스트에는 절약 기록을 추가할 수 없음: wishlistId={}",
                    request.getWishlistId());
            throw new IllegalStateException("완료된 위시리스트에는 절약 기록을 추가할 수 없습니다.");
        }

        // 절약 기록 생성
        Saving saving = Saving.builder()
                .wishlistId(request.getWishlistId())
                .userId(userId)
                .amount(request.getAmount())
                .build();

        Saving savedSaving = savingRepository.save(saving);

        // 위시리스트 금액 업데이트 및 목표 달성 체크
        wishlist.addSavingAmount(request.getAmount());

        log.info("절약 기록 추가 완료: savingId={}, 현재금액={}/{}",
                savedSaving.getSavingId(),
                wishlist.getCurrentAmount(),
                wishlist.getTargetPrice());

        return SavingResponse.from(savedSaving);
    }

    /**
     * 절약 통계 조회 (총 절약 금액)
     */
    public SavingStatisticsResponse getSavingStatistics(Long userId) {
        log.info("절약 통계 조회: userId={}", userId);

        Integer totalAmount = savingRepository.getTotalSavingAmount(userId);

        return SavingStatisticsResponse.of(totalAmount);
    }
}