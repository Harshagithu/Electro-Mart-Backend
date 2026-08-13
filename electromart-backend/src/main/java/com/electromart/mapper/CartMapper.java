package com.electromart.mapper;

import com.electromart.dto.response.CartItemResponse;
import com.electromart.dto.response.CartResponse;
import com.electromart.entity.Cart;
import com.electromart.entity.CartItem;
import com.electromart.util.PriceUtils;

import java.math.BigDecimal;

public final class CartMapper {

    private CartMapper() {}

    public static CartItemResponse toItemResponse(CartItem item) {
        BigDecimal unitPrice = PriceUtils.discountedPrice(item.getProduct());
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productImageUrl(item.getProduct().getImageUrl())
                .unitPrice(unitPrice)
                .quantity(item.getQuantity())
                .lineTotal(lineTotal)
                .availableStock(item.getProduct().getStockQuantity())
                .active(item.getProduct().isActive())
                .build();
    }

    public static CartResponse toResponse(Cart cart) {
        var itemResponses = cart.getItems().stream().map(CartMapper::toItemResponse).toList();
        int totalItems = itemResponses.stream().mapToInt(CartItemResponse::getQuantity).sum();
        BigDecimal subtotal = itemResponses.stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .totalItems(totalItems)
                .subtotal(subtotal)
                .build();
    }
}