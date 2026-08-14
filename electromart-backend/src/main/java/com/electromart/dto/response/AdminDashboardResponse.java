package com.electromart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminDashboardResponse {
    private long totalUsers;
    private long totalProducts;
    private long totalOrders;
    private long ordersToday;
    private BigDecimal totalRevenue;          // realized revenue — DELIVERED orders only
    private Map<String, Long> ordersByStatus;
    private List<LowStockProductResponse> lowStockProducts;
    private List<RecentOrderResponse> recentOrders;
}