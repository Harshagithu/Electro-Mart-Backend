package com.electromart.repository;

import com.electromart.entity.Order;
import com.electromart.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);
    Optional<Order> findByIdAndUserId(Long id, Long userId);
    Optional<Order> findByOrderNumber(String orderNumber);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    long countByStatus(OrderStatus status);
    long countByCreatedAtAfter(LocalDateTime start);

    @Query("select coalesce(sum(o.totalAmount), 0) from Order o where o.status = 'DELIVERED'")
    BigDecimal sumRevenueFromDeliveredOrders();
}