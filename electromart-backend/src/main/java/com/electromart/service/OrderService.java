package com.electromart.service;

import com.electromart.dto.request.CheckoutRequest;
import com.electromart.dto.request.OrderStatusUpdateRequest;
import com.electromart.dto.response.OrderResponse;
import com.electromart.dto.response.PagedResponse;
import com.electromart.entity.User;
import com.electromart.enums.OrderStatus;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse checkout(User user, CheckoutRequest request);
    PagedResponse<OrderResponse> getMyOrders(User user, Pageable pageable);
    OrderResponse getOrderDetail(Long id, User requester);
    OrderResponse cancelOrder(Long id, User user);

    PagedResponse<OrderResponse> adminGetAll(OrderStatus status, Pageable pageable);
    OrderResponse adminUpdateStatus(Long id, OrderStatusUpdateRequest request);
}