package com.unit_wiseb.value_calculator.domain.calculation.service;

import com.unit_wiseb.value_calculator.domain.calculation.dto.CalculationRequest;
import com.unit_wiseb.value_calculator.domain.calculation.dto.CalculationResponse;
import com.unit_wiseb.value_calculator.domain.unit.entity.Unit;
import com.unit_wiseb.value_calculator.domain.unit.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CalculationService {

    private final UnitRepository unitRepository;

    /**
     * 가치 계산
     */
    public CalculationResponse calculate(Long userId, CalculationRequest request) {
        log.info("가치 계산 요청: userId={}, amount={}, unitId={}",
                userId, request.getAmount(), request.getUnitId());

        // 환산 단위 조회
        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> {
                    log.warn("환산 단위를 찾을 수 없음: unitId={}", request.getUnitId());
                    return new IllegalArgumentException("환산 단위를 찾을 수 없습니다.");
                });

        // 커스텀 단위면 사용자의 단위인지 확인하고
        if (!unit.getIsDefault() && !unit.getUserId().equals(userId)) {
            log.warn("권한 없음: userId={}, unitId={}, unitUserId={}",
                    userId, request.getUnitId(), unit.getUserId());
            throw new IllegalArgumentException("해당 환산 단위를 사용할 권한이 없습니다.");
        }

        // 계산: amount / unitPrice (소수점 1자리까지 반올림하도록)
        BigDecimal amount = BigDecimal.valueOf(request.getAmount());
        BigDecimal unitPrice = BigDecimal.valueOf(unit.getUnitPrice());
        BigDecimal result = amount.divide(unitPrice, 1, RoundingMode.HALF_UP);

        log.info("계산 완료: {}원 = {} {} {}",
                request.getAmount(),
                result,
                unit.getUnitName(),
                unit.getUnitCounter());

        return CalculationResponse.builder()
                .amount(request.getAmount())
                .unitId(unit.getId())
                .unitName(unit.getUnitName())
                .unitPrice(unit.getUnitPrice())
                .unitCounter(unit.getUnitCounter())
                .result(result.doubleValue())
                .iconPath(unit.getIcon().getIconPath())
                .iconName(unit.getIcon().getIconName())
                .build();
    }
}