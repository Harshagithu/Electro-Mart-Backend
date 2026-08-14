package com.electromart.mapper;

import com.electromart.dto.response.PaymentResponse;
import com.electromart.entity.Payment;

public final class PaymentMapper {

    private PaymentMapper() {}

    public static PaymentResponse toResponse(Payment payment) {
        if (payment == null) return null;
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .orderNumber(payment.getOrder().getOrderNumber())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .amount(payment.getAmount())
                .paidAt(payment.getPaidAt())
                .build();
    }
}