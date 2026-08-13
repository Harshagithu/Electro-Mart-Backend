package com.electromart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private BigDecimal unitPrice;      // discounted price, what they actually pay per unit
    private int quantity;
    private BigDecimal lineTotal;
    private int availableStock;
    private boolean active;            // false if the product's since been delisted
}