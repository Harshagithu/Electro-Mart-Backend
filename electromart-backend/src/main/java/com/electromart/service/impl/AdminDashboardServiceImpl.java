package com.electromart.service.impl;

import com.electromart.dto.response.AdminDashboardResponse;
import com.electromart.dto.response.LowStockProductResponse;
import com.electromart.dto.response.RecentOrderResponse;
import com.electromart.enums.OrderStatus;
import com.electromart.repository.OrderRepository;
import com.electromart.repository.ProductRepository;
import com.electromart.repository.UserRepository;
import com.electromart.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    // Below this many units left, a product shows up in "low stock." A fixed
    // constant is fine at this scale — could move to application.properties
    // later if it ever needed to be tunable per environment.
    private static final int LOW_STOCK_THRESHOLD = 10;

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard() {
        Map<String, Long> ordersByStatus = new LinkedHashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            ordersByStatus.put(status.name(), orderRepository.countByStatus(status));
        }

        var lowStock = productRepository
                .findTop10ByActiveTrueAndStockQuantityLessThanOrderByStockQuantityAsc(LOW_STOCK_THRESHOLD).stream()
                .map(p -> LowStockProductResponse.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .categoryName(p.getCategory().getName())
                        .stockQuantity(p.getStockQuantity())
                        .build())
                .toList();

        var recentOrders = orderRepository.findTop10ByOrderByPlacedAtDesc().stream()
                .map(o -> RecentOrderResponse.builder()
                        .id(o.getId())
                        .orderNumber(o.getOrderNumber())
                        .customerName(o.getUser().getFirstName() + " " + o.getUser().getLastName())
                        .status(o.getStatus())
                        .totalAmount(o.getTotalAmount())
                        .placedAt(o.getPlacedAt())
                        .build())
                .toList();

        return AdminDashboardResponse.builder()
                .totalUsers(userRepository.count())
                .totalProducts(productRepository.count())
                .totalOrders(orderRepository.count())
                .ordersToday(orderRepository.countByCreatedAtAfter(LocalDate.now().atStartOfDay()))
                .totalRevenue(orderRepository.sumRevenueFromDeliveredOrders())
                .ordersByStatus(ordersByStatus)
                .lowStockProducts(lowStock)
                .recentOrders(recentOrders)
                .build();
    }
}