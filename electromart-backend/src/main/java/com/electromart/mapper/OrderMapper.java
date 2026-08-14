package com.electromart.mapper;

import com.electromart.dto.response.OrderItemResponse;
import com.electromart.dto.response.OrderResponse;
import com.electromart.entity.Order;
import com.electromart.entity.OrderItem;
import com.electromart.entity.Payment;

public final class OrderMapper {

    private OrderMapper() {}

    public static OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productName(item.getProductNameSnapshot())
                .productBrand(item.getProductBrandSnapshot())
                .productImageUrl(item.getProductImageSnapshot())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .lineTotal(item.getLineTotal())
                .build();
    }

    public static OrderResponse toResponse(Order order, Payment payment) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .items(order.getItems().stream().map(OrderMapper::toItemResponse).toList())
                .shippingFullName(order.getShippingFullName())
                .shippingPhone(order.getShippingPhone())
                .shippingAddressLine(order.getShippingAddressLine())
                .shippingCity(order.getShippingCity())
                .shippingState(order.getShippingState())
                .shippingPostalCode(order.getShippingPostalCode())
                .shippingCountry(order.getShippingCountry())
                .subtotal(order.getSubtotal())
                .discountTotal(order.getDiscountTotal())
                .shippingFee(order.getShippingFee())
                .totalAmount(order.getTotalAmount())
                .paymentMethod(payment != null ? payment.getMethod() : null)
                .paymentStatus(payment != null ? payment.getStatus() : null)
                .transactionId(payment != null ? payment.getTransactionId() : null)
                .placedAt(order.getPlacedAt())
                .deliveredAt(order.getDeliveredAt())
                .cancelledAt(order.getCancelledAt())
                .build();
    }
}