package com.electromart.repository;

import com.electromart.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    // JpaSpecificationExecutor is what powers the keyword/category/brand/
    // price/rating filter combinations in the search module (Phase 7) —
    // still zero hand-written SQL, just composed query predicates.
    List<Product> findTop8ByFeaturedTrueAndActiveTrue();
    List<Product> findTop8ByActiveTrueOrderByCreatedAtDesc();
    Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);
    List<Product> findTop8ByCategoryIdAndActiveTrueAndIdNotOrderByRatingDesc(Long categoryId, Long excludeId);
}