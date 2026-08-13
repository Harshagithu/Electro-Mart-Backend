package com.electromart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class WishlistItemResponse {
    private Long productId;
    private String productName;
    private String productImageUrl;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private boolean inStock;
    private LocalDateTime addedAt;
}