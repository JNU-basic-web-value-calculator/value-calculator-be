package com.unit_wiseb.value_calculator.domain.wishlist.repository;

import com.unit_wiseb.value_calculator.domain.wishlist.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    /**
     * 특정 사용자의 모든 위시리스트 조회 (최신순)
     */
    List<Wishlist> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 특정 사용자의 위시리스트 조회 (위시리스트 ID)
     */
    Optional<Wishlist> findByWishlistIdAndUserId(Long wishlistId, Long userId);

    /**
     * 특정 사용자의 진행 중인 위시리스트 조회
     */
    List<Wishlist> findByUserIdAndIsCompletedFalseOrderByCreatedAtDesc(Long userId);

    /**
     * TODO 쓸지 안쓸지 확인할것. -> 주석처리 여부 결정
     * 특정 사용자의 완료된 위시리스트 조회
     */
    List<Wishlist> findByUserIdAndIsCompletedTrueOrderByCompletedAtDesc(Long userId);
}