package com.unit_wiseb.value_calculator.domain.wishlist.controller;

import com.unit_wiseb.value_calculator.domain.common.annotation.CurrentUserId;
import com.unit_wiseb.value_calculator.domain.wishlist.dto.request.WishlistCreateRequest;
import com.unit_wiseb.value_calculator.domain.wishlist.dto.request.WishlistUpdateRequest;
import com.unit_wiseb.value_calculator.domain.wishlist.dto.response.WishlistDetailResponse;
import com.unit_wiseb.value_calculator.domain.wishlist.dto.response.WishlistResponse;
import com.unit_wiseb.value_calculator.domain.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "위시리스트 API")
@SecurityRequirement(name = "bearerAuth")
public class WishlistController {

    private final WishlistService wishlistService;

    /**
     * 위시리스트 생성
     * POST /api/wishlists
     */
    @PostMapping
    @Operation(summary = "위시리스트 생성", description = "새로운 위시리스트를 생성합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "위시리스트 생성 성공",
                    content = @Content(schema = @Schema(implementation = WishlistResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<WishlistResponse> createWishlist(
            @Parameter(hidden = true) @CurrentUserId Long userId,
            @Valid @RequestBody WishlistCreateRequest request
    ) {
        WishlistResponse response = wishlistService.createWishlist(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 진행 중인 위시리스트 조회
     * GET /api/wishlists/my/in-progress
     */
    @GetMapping("/my/in-progress")
    @Operation(summary = "진행 중인 위시리스트 조회", description = "목표를 달성하지 못한 위시리스트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = WishlistResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<WishlistResponse>> getInProgressWishlists(
            @Parameter(hidden = true) @CurrentUserId Long userId
    ) {
        List<WishlistResponse> response = wishlistService.getInProgressWishlists(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 위시리스트 상세 조회
     * GET /api/wishlists/{wishlistId}
     */
    @GetMapping("/{wishlistId}")
    @Operation(summary = "위시리스트 상세 조회", description = "특정 위시리스트의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = WishlistDetailResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "위시리스트를 찾을 수 없음")
    })
    public ResponseEntity<WishlistDetailResponse> getWishlistDetail(
            @Parameter(hidden = true) @CurrentUserId Long userId,
            @Parameter(description = "위시리스트 ID", example = "1") @PathVariable Long wishlistId
    ) {
        WishlistDetailResponse response = wishlistService.getWishlistDetail(userId, wishlistId);
        return ResponseEntity.ok(response);
    }


    /**
     * 위시리스트 수정
     * PUT /api/wishlists/{wishlistId}
     */
    @PutMapping("/{wishlistId}")
    @Operation(summary = "위시리스트 수정", description = "위시리스트 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = WishlistResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "위시리스트를 찾을 수 없음")
    })
    public ResponseEntity<WishlistResponse> updateWishlist(
            @Parameter(hidden = true) @CurrentUserId Long userId,
            @Parameter(description = "위시리스트 ID", example = "1") @PathVariable Long wishlistId,
            @Valid @RequestBody WishlistUpdateRequest request
    ) {
        WishlistResponse response = wishlistService.updateWishlist(userId, wishlistId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 위시리스트 삭제
     * DELETE /api/wishlists/{wishlistId}
     */
    @DeleteMapping("/{wishlistId}")
    @Operation(summary = "위시리스트 삭제", description = "위시리스트를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "위시리스트를 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteWishlist(
            @Parameter(hidden = true) @CurrentUserId Long userId,
            @Parameter(description = "위시리스트 ID", example = "1") @PathVariable Long wishlistId
    ) {
        wishlistService.deleteWishlist(userId, wishlistId);
        return ResponseEntity.noContent().build();
    }


    /**
     * 내 위시리스트 목록 조회 (전체)
     * GET /api/wishlists/my
     */
//    @GetMapping("/my")
//    @Operation(summary = "내 위시리스트 목록 조회", description = "내 모든 위시리스트를 조회합니다. (최신순)")
//    @ApiResponses({
//            @ApiResponse(
//                    responseCode = "200",
//                    description = "조회 성공",
//                    content = @Content(schema = @Schema(implementation = WishlistResponse.class))
//            ),
//            @ApiResponse(responseCode = "401", description = "인증 실패")
//    })
//    public ResponseEntity<List<WishlistResponse>> getMyWishlists(
//            @Parameter(hidden = true) @CurrentUserId Long userId
//    ) {
//        List<WishlistResponse> response = wishlistService.getMyWishlists(userId);
//        return ResponseEntity.ok(response);
//    }


    /**
     * 완료된 위시리스트 조회
     * GET /api/wishlists/my/completed
     */
//    @GetMapping("/my/completed")
//    @Operation(summary = "완료된 위시리스트 조회", description = "목표를 달성한 위시리스트를 조회합니다.")
//    @ApiResponses({
//            @ApiResponse(
//                    responseCode = "200",
//                    description = "조회 성공",
//                    content = @Content(schema = @Schema(implementation = WishlistResponse.class))
//            ),
//            @ApiResponse(responseCode = "401", description = "인증 실패")
//    })
//    public ResponseEntity<List<WishlistResponse>> getCompletedWishlists(
//            @Parameter(hidden = true) @CurrentUserId Long userId
//    ) {
//        List<WishlistResponse> response = wishlistService.getCompletedWishlists(userId);
//        return ResponseEntity.ok(response);
//    }
}