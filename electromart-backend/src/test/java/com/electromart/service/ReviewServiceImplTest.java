package com.electromart.service;

import com.electromart.dto.request.ReviewRequest;
import com.electromart.entity.*;
import com.electromart.enums.OrderStatus;
import com.electromart.exception.ForbiddenException;
import com.electromart.exception.InvalidOrderStateException;
import com.electromart.repository.OrderItemRepository;
import com.electromart.repository.ProductRepository;
import com.electromart.repository.ReviewRepository;
import com.electromart.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private ProductRepository productRepository;

    private ReviewServiceImpl reviewService() {
        return new ReviewServiceImpl(reviewRepository, orderItemRepository, productRepository);
    }

    @Test
    void cannotReviewSomeoneElsesOrder() {
        User owner = User.builder().id(1L).build();
        User impostor = User.builder().id(2L).build();
        Order order = Order.builder().id(100L).user(owner).status(OrderStatus.DELIVERED).build();
        OrderItem orderItem = OrderItem.builder().id(5L).order(order).build();

        when(orderItemRepository.findById(5L)).thenReturn(Optional.of(orderItem));

        ReviewRequest request = new ReviewRequest();
        request.setOrderItemId(5L);
        request.setRating(5);

        assertThatThrownBy(() -> reviewService().create(impostor, request))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void cannotReviewBeforeOrderIsDelivered() {
        User owner = User.builder().id(1L).build();
        Order order = Order.builder().id(100L).user(owner).status(OrderStatus.SHIPPED).build(); // not delivered yet
        OrderItem orderItem = OrderItem.builder().id(5L).order(order).build();

        when(orderItemRepository.findById(5L)).thenReturn(Optional.of(orderItem));

        ReviewRequest request = new ReviewRequest();
        request.setOrderItemId(5L);
        request.setRating(4);

        assertThatThrownBy(() -> reviewService().create(owner, request))
                .isInstanceOf(InvalidOrderStateException.class);
    }
}