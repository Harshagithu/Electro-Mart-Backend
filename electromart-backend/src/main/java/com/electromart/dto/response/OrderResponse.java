package com.electromart.dto.response;

import com.electromart.enums.OrderStatus;
import com.electromart.enums.PaymentMethod;
import com.electromart.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private List<OrderItemResponse> items;

    private String shippingFullName;
    private String shippingPhone;
    private String shippingAddressLine;
    private String shippingCity;
    private String shippingState;
    private String shippingPostalCode;
    private String shippingCountry;

    private BigDecimal subtotal;
    private BigDecimal discountTotal;
    private BigDecimal shippingFee;
    private BigDecimal totalAmount;

    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String transactionId;

    private LocalDateTime placedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
}