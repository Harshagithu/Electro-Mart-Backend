package com.electromart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String brand;
    private BigDecimal price;
    private BigDecimal discountPercentage;
    private BigDecimal discountedPrice;   // computed — what the customer actually pays
    private Integer stockQuantity;
    private boolean inStock;              // computed — stockQuantity > 0
    private String imageUrl;
    private BigDecimal rating;
    private Integer reviewCount;
    private boolean featured;
    private boolean active;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
}