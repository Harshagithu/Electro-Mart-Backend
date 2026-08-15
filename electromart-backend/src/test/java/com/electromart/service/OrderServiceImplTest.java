package com.electromart.service;

import com.electromart.dto.request.CheckoutRequest;
import com.electromart.dto.request.OrderStatusUpdateRequest;
import com.electromart.entity.*;
import com.electromart.enums.OrderStatus;
import com.electromart.enums.PaymentMethod;
import com.electromart.exception.InvalidOrderStateException;
import com.electromart.repository.*;
import com.electromart.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private CartRepository cartRepository;
    @Mock private AddressRepository addressRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private ProductRepository productRepository;

    private OrderServiceImpl orderService() {
        return new OrderServiceImpl(cartRepository, addressRepository, orderRepository, paymentRepository, productRepository);
    }

    @Test
    void checkoutWithAnEmptyCartIsRejected() {
        User user = User.builder().id(1L).build();
        Cart emptyCart = Cart.builder().id(1L).user(user).items(new ArrayList<>()).build();
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(emptyCart));

        CheckoutRequest request = new CheckoutRequest();
        request.setAddressId(1L);
        request.setPaymentMethod(PaymentMethod.COD);

        assertThatThrownBy(() -> orderService().checkout(user, request))
                .isInstanceOf(InvalidOrderStateException.class);
    }

    @Test
    void shippedOrderCannotSkipStraightToDelivered() {
        // SHIPPED -> DELIVERED is actually legal; testing the illegal jump instead:
        // SHIPPED can only go to OUT_FOR_DELIVERY, not directly to DELIVERED.
        Order order = Order.builder().id(1L).status(OrderStatus.SHIPPED).items(new ArrayList<>()).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();
        request.setStatus(OrderStatus.DELIVERED);

        assertThatThrownBy(() -> orderService().adminUpdateStatus(1L, request))
                .isInstanceOf(InvalidOrderStateException.class);
    }

    @Test
    void cancelledOrderCannotBeCancelledAgain() {
        Order order = Order.builder().id(1L).status(OrderStatus.CANCELLED).items(new ArrayList<>()).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();
        request.setStatus(OrderStatus.CANCELLED);

        assertThatThrownBy(() -> orderService().adminUpdateStatus(1L, request))
                .isInstanceOf(InvalidOrderStateException.class);
    }
}