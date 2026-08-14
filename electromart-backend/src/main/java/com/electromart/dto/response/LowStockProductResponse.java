package com.electromart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class LowStockProductResponse {
    private Long id;
    private String name;
    private String categoryName;
    private Integer stockQuantity;
}