package com.electromart.controller;

import com.electromart.dto.response.WishlistResponse;
import com.electromart.security.CurrentUserProvider;
import com.electromart.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public ResponseEntity<WishlistResponse> getWishlist() {
        return ResponseEntity.ok(wishlistService.getWishlist(currentUserProvider.getCurrentUser()));
    }

    @PostMapping("/items/{productId}")
    public ResponseEntity<WishlistResponse> addItem(@PathVariable Long productId) {
        return ResponseEntity.ok(wishlistService.addItem(currentUserProvider.getCurrentUser(), productId));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<WishlistResponse> removeItem(@PathVariable Long productId) {
        return ResponseEntity.ok(wishlistService.removeItem(currentUserProvider.getCurrentUser(), productId));
    }
}