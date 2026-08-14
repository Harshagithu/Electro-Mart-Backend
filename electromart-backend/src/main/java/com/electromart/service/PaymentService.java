package com.electromart.service;

import com.electromart.dto.response.PaymentResponse;
import com.electromart.entity.User;

public interface PaymentService {
    PaymentResponse getByOrderId(Long orderId, User requester);
}