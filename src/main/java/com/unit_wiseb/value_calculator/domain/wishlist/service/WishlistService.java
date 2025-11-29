package com.unit_wiseb.value_calculator.domain.wishlist.service;

import com.unit_wiseb.value_calculator.domain.wishlist.dto.request.WishlistCreateRequest;
import com.unit_wiseb.value_calculator.domain.wishlist.dto.request.WishlistUpdateRequest;
import com.unit_wiseb.value_calculator.domain.wishlist.dto.response.WishlistDetailResponse;
import com.unit_wiseb.value_calculator.domain.wishlist.dto.response.WishlistResponse;
import com.unit_wiseb.value_calculator.domain.wishlist.entity.Wishlist;
import com.unit_wiseb.value_calculator.domain.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class WishlistService {

    private final WishlistRepository wishlistRepository;

    /**
     * 위시리스트 생성
     */
    @Transactional
    public WishlistResponse createWishlist(Long userId, WishlistCreateRequest request) {
        log.info("위시리스트 생성 요청: userId={}, itemName={}", userId, request.getItemName());

        // 위시리스트 엔티티 생성
        Wishlist wishlist = Wishlist.builder()
                .userId(userId)
                .itemName(request.getItemName())
                .targetPrice(request.getTargetPrice())
                .itemUrl(request.getItemUrl())
                .currentAmount(0)
                .isCompleted(false)
                .build();

        Wishlist savedWishlist = wishlistRepository.save(wishlist);

        log.info("위시리스트 생성 완료: wishlistId={}", savedWishlist.getWishlistId());

        return WishlistResponse.from(savedWishlist);
    }

    /**
     * 진행 중인 위시리스트 조회
     */
    public List<WishlistResponse> getInProgressWishlists(Long userId) {
        log.info("진행 중인 위시리스트 조회: userId={}", userId);

        List<Wishlist> wishlists = wishlistRepository.findByUserIdAndIsCompletedFalseOrderByCreatedAtDesc(userId);

        return wishlists.stream()
                .map(WishlistResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 위시리스트 상세 조회
     */
    public WishlistDetailResponse getWishlistDetail(Long userId, Long wishlistId) {
        log.info("위시리스트 상세 조회: userId={}, wishlistId={}", userId, wishlistId);

        Wishlist wishlist = wishlistRepository.findByWishlistIdAndUserId(wishlistId, userId)
                .orElseThrow(() -> {
                    log.warn("위시리스트를 찾을 수 없음: wishlistId={}, userId={}", wishlistId, userId);
                    return new IllegalArgumentException("위시리스트를 찾을 수 없습니다.");
                });

        return WishlistDetailResponse.from(wishlist);
    }

    /**
     * 위시리스트 수정
     */
    @Transactional
    public WishlistResponse updateWishlist(Long userId, Long wishlistId, WishlistUpdateRequest request) {
        log.info("위시리스트 수정 요청: userId={}, wishlistId={}", userId, wishlistId);

        // 위시리스트 조회 및 권한 확인
        Wishlist wishlist = wishlistRepository.findByWishlistIdAndUserId(wishlistId, userId)
                .orElseThrow(() -> {
                    log.warn("위시리스트를 찾을 수 없음: wishlistId={}, userId={}", wishlistId, userId);
                    return new IllegalArgumentException("위시리스트를 찾을 수 없습니다.");
                });

        // 위시리스트 수정 (엔티티의 비즈니스 메서드 사용)
        wishlist.updateWishlist(
                request.getItemName(),
                request.getTargetPrice(),
                request.getItemUrl()
        );

        log.info("위시리스트 수정 완료: wishlistId={}", wishlistId);

        return WishlistResponse.from(wishlist);
    }

    /**
     * 위시리스트 삭제
     */
    @Transactional
    public void deleteWishlist(Long userId, Long wishlistId) {
        log.info("위시리스트 삭제 요청: userId={}, wishlistId={}", userId, wishlistId);

        // 위시리스트 조회 및 권한 확인
        Wishlist wishlist = wishlistRepository.findByWishlistIdAndUserId(wishlistId, userId)
                .orElseThrow(() -> {
                    log.warn("위시리스트를 찾을 수 없음: wishlistId={}, userId={}", wishlistId, userId);
                    return new IllegalArgumentException("위시리스트를 찾을 수 없습니다.");
                });

        wishlistRepository.delete(wishlist);

        log.info("위시리스트 삭제 완료: wishlistId={}", wishlistId);
    }





    //TODO 기능정의 화인할 것

    /**
     * 내 위시리스트 목록 조회 (전체)
     */
    public List<WishlistResponse> getMyWishlists(Long userId) {
        log.info("위시리스트 목록 조회: userId={}", userId);

        List<Wishlist> wishlists = wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return wishlists.stream()
                .map(WishlistResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 완료된 위시리스트 조회
     */
    public List<WishlistResponse> getCompletedWishlists(Long userId) {
        log.info("완료된 위시리스트 조회: userId={}", userId);

        List<Wishlist> wishlists = wishlistRepository.findByUserIdAndIsCompletedTrueOrderByCompletedAtDesc(userId);

        return wishlists.stream()
                .map(WishlistResponse::from)
                .collect(Collectors.toList());
    }
}