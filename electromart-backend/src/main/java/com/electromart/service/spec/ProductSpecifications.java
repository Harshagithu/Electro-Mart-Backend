package com.electromart.service.spec;

import com.electromart.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class ProductSpecifications {

    private ProductSpecifications() {}

    public static Specification<Product> build(String keyword, String category, String brand,
                                                 BigDecimal minPrice, BigDecimal maxPrice,
                                                 BigDecimal minRating, boolean activeOnly) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (activeOnly) {
                predicate = cb.and(predicate, cb.isTrue(root.get("active")));
            }
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("description")), like),
                        cb.like(cb.lower(root.get("brand")), like)
                ));
            }
            if (category != null && !category.isBlank()) {
                predicate = cb.and(predicate, cb.equal(cb.lower(root.get("category").get("name")), category.toLowerCase()));
            }
            if (brand != null && !brand.isBlank()) {
                predicate = cb.and(predicate, cb.equal(cb.lower(root.get("brand")), brand.toLowerCase()));
            }
            if (minPrice != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            if (minRating != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("rating"), minRating));
            }
            return predicate;
        };
    }
}