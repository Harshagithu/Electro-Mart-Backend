package com.electromart.controller;

import com.electromart.dto.request.CheckoutRequest;
import com.electromart.dto.response.OrderResponse;
import com.electromart.security.CurrentUserProvider;
import com.electromart.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final OrderService orderService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    public ResponseEntity<OrderResponse> checkout(@Valid @RequestBody CheckoutRequest request) {
        var order = orderService.checkout(currentUserProvider.getCurrentUser(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}