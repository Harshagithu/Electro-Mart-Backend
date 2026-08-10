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
    long countByStatus(OrderStatus status);
    long countByCreatedAtAfter(LocalDateTime start);

    // The one @Query in this whole layer — it's JPQL (queries entity fields
    // like `o.totalAmount`, not table/column names), not raw SQL, so it's
    // still within "let Spring Data do the querying." Derived method names
    // can't express an aggregate sum, which is the only reason this isn't
    // a findBy... method like everything else here.
    @Query("select coalesce(sum(o.totalAmount), 0) from Order o where o.status = 'DELIVERED'")
    BigDecimal sumRevenueFromDeliveredOrders();
}