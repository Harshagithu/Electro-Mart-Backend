package com.electromart.controller;

import com.electromart.dto.response.OrderResponse;
import com.electromart.dto.response.PagedResponse;
import com.electromart.security.CurrentUserProvider;
import com.electromart.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public ResponseEntity<PagedResponse<OrderResponse>> getMyOrders(
            @PageableDefault(size = 10, sort = "placedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(orderService.getMyOrders(currentUserProvider.getCurrentUser(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderDetail(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderDetail(id, currentUserProvider.getCurrentUser()));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id, currentUserProvider.getCurrentUser()));
    }
}