package com.electromart.service.impl;

import com.electromart.dto.response.PaymentResponse;
import com.electromart.entity.Order;
import com.electromart.entity.Payment;
import com.electromart.entity.User;
import com.electromart.enums.RoleName;
import com.electromart.exception.ForbiddenException;
import com.electromart.exception.ResourceNotFoundException;
import com.electromart.mapper.PaymentMapper;
import com.electromart.repository.OrderRepository;
import com.electromart.repository.PaymentRepository;
import com.electromart.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getByOrderId(Long orderId, User requester) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderId));

        boolean isOwner = order.getUser().getId().equals(requester.getId());
        boolean isAdmin = requester.getRoles().stream().anyMatch(r -> r.getName() == RoleName.ADMIN);
        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("You can only view payment details for your own orders");
        }

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No payment record found for this order"));

        return PaymentMapper.toResponse(payment);
    }
}