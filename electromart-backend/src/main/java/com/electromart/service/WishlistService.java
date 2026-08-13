package com.electromart.service;

import com.electromart.dto.response.WishlistResponse;
import com.electromart.entity.User;

public interface WishlistService {
    WishlistResponse getWishlist(User user);
    WishlistResponse addItem(User user, Long productId);
    WishlistResponse removeItem(User user, Long productId);
}