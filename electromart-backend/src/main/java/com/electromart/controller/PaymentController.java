package com.electromart.controller;

import com.electromart.dto.response.PaymentResponse;
import com.electromart.security.CurrentUserProvider;
import com.electromart.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders/{orderId}/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getByOrderId(orderId, currentUserProvider.getCurrentUser()));
    }
}