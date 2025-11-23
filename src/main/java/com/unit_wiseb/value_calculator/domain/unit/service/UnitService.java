package com.unit_wiseb.value_calculator.domain.unit.service;

import com.unit_wiseb.value_calculator.domain.unit.dto.UnitIconResponse;
import com.unit_wiseb.value_calculator.domain.unit.dto.UnitRequest;
import com.unit_wiseb.value_calculator.domain.unit.dto.UnitResponse;
import com.unit_wiseb.value_calculator.domain.unit.entity.Unit;
import com.unit_wiseb.value_calculator.domain.unit.entity.UnitIcon;
import com.unit_wiseb.value_calculator.domain.unit.repository.UnitIconRepository;
import com.unit_wiseb.value_calculator.domain.unit.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;
    private final UnitIconRepository unitIconRepository;

    /**
     * 기본 단위 목록 조회
     */
    @Transactional(readOnly = true)
    public List<UnitResponse> getDefaultUnits() {
        log.info("기본 단위 목록 조회");

        List<Unit> units = unitRepository.findByUserIdIsNullAndIsDefaultTrue();

        return units.stream()
                .map(UnitResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 커스텀 단위 목록 조회
     * 특정 사용자가 생성한 커스텀 단위들
     */
    @Transactional(readOnly = true)
    public List<UnitResponse> getMyCustomUnits(Long userId) {
        log.info("사용자 커스텀 단위 목록 조회: userId={}", userId);

        List<Unit> units = unitRepository.findByUserId(userId);

        return units.stream()
                .map(UnitResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 모든 단위 조회 (기본 + 커스텀)
     * 기본 단위와 사용자가 만든 커스텀 단위를 모두 포함
     */
    @Transactional(readOnly = true)
    public List<UnitResponse> getAllUnitsForUser(Long userId) {
        log.info("사용자의 모든 단위 조회: userId={}", userId);

        List<Unit> units = unitRepository.findByUserIdIsNullAndIsDefaultTrueOrUserId(userId);

        return units.stream()
                .map(UnitResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 커스텀 단위 생성
     * 사용자가 새로운 환산 단위를 생성
     */
    @Transactional
    public UnitResponse createCustomUnit(Long userId, UnitRequest request) {
        log.info("커스텀 단위 생성: userId={}, unitName={}", userId, request.getUnitName());

        // 아이콘 존재 여부 확인
        UnitIcon icon = unitIconRepository.findById(request.getIconId())
                .orElseThrow(() -> {
                    log.error("존재하지 않는 아이콘: iconId={}", request.getIconId());
                    return new IllegalArgumentException("존재하지 않는 아이콘입니다.");
                });

        // 커스텀 단위 생성
        Unit unit = Unit.builder()
                .userId(userId)
                .iconId(request.getIconId())
                .unitName(request.getUnitName())
                .unitPrice(request.getUnitPrice())
                .unitCounter(request.getUnitCounter())
                .isDefault(false)  // 커스텀 단위는 기본 단위가 아님
                .build();

        Unit savedUnit = unitRepository.save(unit);

        log.info("커스텀 단위 생성 완료: unitId={}", savedUnit.getId());

        return UnitResponse.from(savedUnit);
    }

    /**
     * 커스텀 단위 수정
     * 사용자가 생성한 커스텀 단위만 수정 가능 (기본 단위는 수정 불가)
     */
    @Transactional
    public UnitResponse updateCustomUnit(Long userId, Long unitId, UnitRequest request) {
        log.info("커스텀 단위 수정: userId={}, unitId={}", userId, unitId);

        // 단위 조회
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 단위: unitId={}", unitId);
                    return new IllegalArgumentException("존재하지 않는 단위입니다.");
                });

        // 권한 확인 >>> 본인의 커스텀 단위만 수정 가능함
        if (unit.getIsDefault()) {
            log.error("기본 단위 수정 시도: unitId={}", unitId);
            throw new IllegalArgumentException("기본 단위는 수정할 수 없습니다.");
        }

        if (!unit.getUserId().equals(userId)) {
            log.error("권한 없는 단위 수정 시도: userId={}, unitId={}", userId, unitId);
            throw new IllegalArgumentException("본인의 단위만 수정할 수 있습니다.");
        }

        // 아이콘 존재 여부 확인
        unitIconRepository.findById(request.getIconId())
                .orElseThrow(() -> {
                    log.error("존재하지 않는 아이콘: iconId={}", request.getIconId());
                    return new IllegalArgumentException("존재하지 않는 아이콘입니다.");
                });

        // 단위 정보 업데이트
        unit.updateCustomUnit(
                request.getUnitName(),
                request.getUnitPrice(),
                request.getUnitCounter(),
                request.getIconId()
        );

        log.info("커스텀 단위 수정 완료: unitId={}", unitId);

        return UnitResponse.from(unit);
    }

    /**
     * 커스텀 단위 삭제
     * 사용자가 생성한 커스텀 단위만 삭제 가능 (기본 단위는 삭제 불가)
     */
    @Transactional
    public void deleteCustomUnit(Long userId, Long unitId) {
        log.info("커스텀 단위 삭제: userId={}, unitId={}", userId, unitId);

        // 단위 조회
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 단위: unitId={}", unitId);
                    return new IllegalArgumentException("존재하지 않는 단위입니다.");
                });

        // 권한 확인 (본인의 커스텀 단위만 삭제 가능)
        if (unit.getIsDefault()) {
            log.error("기본 단위 삭제 시도: unitId={}", unitId);
            throw new IllegalArgumentException("기본 단위는 삭제할 수 없습니다.");
        }

        if (!unit.getUserId().equals(userId)) {
            log.error("권한 없는 단위 삭제 시도: userId={}, unitId={}", userId, unitId);
            throw new IllegalArgumentException("본인의 단위만 삭제할 수 있습니다.");
        }

        // 단위 삭제
        unitRepository.delete(unit);

        log.info("커스텀 단위 삭제 완료: unitId={}", unitId);
    }

    //TODO 커스텀 단위 생성할 떄 아이콘 선택할때 필요한 아이콘 목록 조회 만들것
    /**
     * 모든 아이콘 목록 조회
     * 커스텀 단위 생성 시 아이콘 선택 때  사용할거임
     */
    @Transactional(readOnly = true)
    public List<UnitIconResponse> getAllIcons() {
        log.info("아이콘 목록 조회");

        List<UnitIcon> icons = unitIconRepository.findAll();

        return icons.stream()
                .map(UnitIconResponse::from)
                .collect(Collectors.toList());
    }
}