package com.unit_wiseb.value_calculator.domain.unit.controller;

import com.unit_wiseb.value_calculator.domain.common.annotation.CurrentUserId;
import com.unit_wiseb.value_calculator.domain.unit.dto.UnitIconResponse;
import com.unit_wiseb.value_calculator.domain.unit.dto.UnitRequest;
import com.unit_wiseb.value_calculator.domain.unit.dto.UnitResponse;
import com.unit_wiseb.value_calculator.domain.unit.service.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 환산 단위 API 컨트롤러
 * 단위 조회, 생성, 수정, 삭제
 */
@Tag(name = "Unit", description = "환산 단위 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    /**
     * 기본 단위 목록 조회
     * GET /api/units/default
     */
    @Operation(
            summary = "기본 단위 목록 조회",
            description = "앱에서 제공하는 기본 환산 단위 목록을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/default")
    public ResponseEntity<List<UnitResponse>> getDefaultUnits() {
        log.info("기본 단위 목록 조회 요청");

        List<UnitResponse> units = unitService.getDefaultUnits();

        return ResponseEntity.ok(units);
    }

    /**
     * 내 커스텀 단위 목록 조회
     * GET /api/units/my
     */
    @Operation(
            summary = "내 커스텀 단위 목록 조회",
            description = "사용자가 생성한 커스텀 환산 단위 목록을 조회합니다.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/my")
    public ResponseEntity<List<UnitResponse>> getMyCustomUnits(
            @Parameter(hidden = true) @CurrentUserId Long userId
    ) {
        log.info("커스텀 단위 목록 조회 요청: userId={}", userId);

        List<UnitResponse> units = unitService.getMyCustomUnits(userId);
        return ResponseEntity.ok(units);
    }

    /**
     * 모든 단위 조회 (기본 + 커스텀)
     * GET /api/units/all
     */
    @Operation(
            summary = "모든 단위 조회",
            description = "기본 단위와 사용자의 커스텀 단위를 모두 조회합니다.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/all")
    public ResponseEntity<List<UnitResponse>> getAllUnitsForUser(
            @Parameter(hidden = true) @CurrentUserId Long userId
    ) {
        log.info("모든 단위 조회 요청: userId={}", userId);

        List<UnitResponse> units = unitService.getAllUnitsForUser(userId);


        return ResponseEntity.ok(units);
    }

    /**
     * 커스텀 단위 생성
     * POST /api/units
     */
    @Operation(
            summary = "커스텀 단위 생성",
            description = "사용자가 새로운 커스텀 환산 단위를 생성합니다.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<UnitResponse> createCustomUnit(
            @Parameter(hidden = true) @CurrentUserId Long userId,
            @Parameter(description = "단위 생성 요청", required = true)
            @Valid @RequestBody UnitRequest request
    ) {
        log.info("커스텀 단위 생성 요청: userId={}, unitName={}", userId, request.getUnitName());

        UnitResponse unit = unitService.createCustomUnit(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(unit);
    }

    /**
     * 커스텀 단위 수정 (기본 단위는 수정 불가)
     * PUT /api/units/{unitId}
     */
    @Operation(
            summary = "커스텀 단위 수정",
            description = "사용자가 생성한 커스텀 단위를 수정합니다. 기본 단위는 수정할 수 없습니다.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (기본 단위 수정 시도, 권한 없음)"),
            @ApiResponse(responseCode = "404", description = "단위를 찾을 수 없음")
    })
    @PutMapping("/{unitId}")
    public ResponseEntity<?> updateCustomUnit(
            @Parameter(hidden = true) @CurrentUserId Long userId,
            @Parameter(description = "단위 ID", required = true)
            @PathVariable Long unitId,
            @Parameter(description = "단위 수정 요청", required = true)
            @Valid @RequestBody UnitRequest request
    ) {
        log.info("커스텀 단위 수정 요청: userId={}, unitId={}", userId, unitId);

        try {
            UnitResponse unit = unitService.updateCustomUnit(userId, unitId, request);
            return ResponseEntity.ok(unit);
        } catch (IllegalArgumentException e) {
            log.error("단위 수정 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 커스텀 단위 삭제 (기본 단위는 삭제 불가)
     * DELETE /api/units/{unitId}
     */
    @Operation(
            summary = "커스텀 단위 삭제",
            description = "사용자가 생성한 커스텀 단위를 삭제합니다. 기본 단위는 삭제할 수 없습니다.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (기본 단위 삭제 시도, 권한 없음)"),
            @ApiResponse(responseCode = "404", description = "단위를 찾을 수 없음")
    })
    @DeleteMapping("/{unitId}")
    public ResponseEntity<String> deleteCustomUnit(
            @Parameter(hidden = true) @CurrentUserId Long userId,
            @Parameter(description = "단위 ID", required = true)
            @PathVariable Long unitId
    ) {
        log.info("커스텀 단위 삭제 요청: userId={}, unitId={}", userId, unitId);

        try {
            unitService.deleteCustomUnit(userId, unitId);
            return ResponseEntity.ok("커스텀 단위 삭제 성공");
        } catch (IllegalArgumentException e) {
            log.error("단위 삭제 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 아이콘 목록 조회
     * GET /api/units/icons
     */
    @Operation(
            summary = "아이콘 목록 조회",
            description = "커스텀 단위 생성 시 선택 가능한 아이콘 목록을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/icons")
    public ResponseEntity<List<UnitIconResponse>> getAllIcons() {
        log.info("아이콘 목록 조회 요청");

        List<UnitIconResponse> icons = unitService.getAllIcons();

        return ResponseEntity.ok(icons);
    }
}






