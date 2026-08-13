package com.electromart.mapper;

import java.math.BigDecimal;

import com.electromart.dto.response.ProductResponse;
import com.electromart.entity.Product;
import com.electromart.util.PriceUtils;

public final class ProductMapper {

    private ProductMapper() {}

    public static ProductResponse toResponse(Product product) {
        if (product == null) return null;
        BigDecimal discountedPrice = PriceUtils.discountedPrice(product);
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .brand(product.getBrand())
                .price(product.getPrice())
                .discountPercentage(product.getDiscountPercentage())
                .discountedPrice(discountedPrice)
                .stockQuantity(product.getStockQuantity())
                .inStock(product.getStockQuantity() != null && product.getStockQuantity() > 0)
                .imageUrl(product.getImageUrl())
                .rating(product.getRating())
                .reviewCount(product.getReviewCount())
                .featured(product.isFeatured())
                .active(product.isActive())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .createdAt(product.getCreatedAt())
                .build();
    }
}