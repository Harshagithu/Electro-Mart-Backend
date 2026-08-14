package com.electromart.dto.response;

import com.electromart.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class RecentOrderResponse {
    private Long id;
    private String orderNumber;
    private String customerName;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime placedAt;
}