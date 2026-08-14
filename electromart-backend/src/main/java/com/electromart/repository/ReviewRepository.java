package com.electromart.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.electromart.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByProductId(Long productId, Pageable pageable);
    boolean existsByOrderItemId(Long orderItemId);
    @Query("select coalesce(avg(r.rating), 0) from Review r where r.product.id = :productId")
    Double averageRatingForProduct(@Param("productId") Long productId);
    long countByProductId(Long productId);
}