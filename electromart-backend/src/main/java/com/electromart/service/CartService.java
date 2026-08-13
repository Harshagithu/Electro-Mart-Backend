package com.electromart.service;

import com.electromart.dto.request.AddCartItemRequest;
import com.electromart.dto.request.UpdateCartItemRequest;
import com.electromart.dto.response.CartResponse;
import com.electromart.entity.User;

public interface CartService {
    CartResponse getCart(User user);
    CartResponse addItem(User user, AddCartItemRequest request);
    CartResponse updateQuantity(User user, Long itemId, UpdateCartItemRequest request);
    CartResponse removeItem(User user, Long itemId);
    void clearCart(User user);
}