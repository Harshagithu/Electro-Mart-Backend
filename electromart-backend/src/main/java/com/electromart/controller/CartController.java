package com.electromart.controller;

import com.electromart.dto.request.AddCartItemRequest;
import com.electromart.dto.request.UpdateCartItemRequest;
import com.electromart.dto.response.CartResponse;
import com.electromart.security.CurrentUserProvider;
import com.electromart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(cartService.getCart(currentUserProvider.getCurrentUser()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@Valid @RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(currentUserProvider.getCurrentUser(), request));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateQuantity(@PathVariable Long itemId,
                                                          @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateQuantity(currentUserProvider.getCurrentUser(), itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItem(currentUserProvider.getCurrentUser(), itemId));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart(currentUserProvider.getCurrentUser());
        return ResponseEntity.noContent().build();
    }
}