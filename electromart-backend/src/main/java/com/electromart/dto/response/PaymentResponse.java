package com.electromart.dto.response;

import com.electromart.enums.PaymentMethod;
import com.electromart.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private PaymentMethod method;
    private PaymentStatus status;
    private String transactionId;
    private BigDecimal amount;
    private LocalDateTime paidAt;
}