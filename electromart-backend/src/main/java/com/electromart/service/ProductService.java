package com.electromart.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.electromart.dto.request.ProductRequest;
import com.electromart.dto.response.PagedResponse;
import com.electromart.dto.response.ProductResponse;

public interface ProductService {
    ProductResponse create(ProductRequest request);
    ProductResponse update(Long id, ProductRequest request);
    void delete(Long id);
    ProductResponse getById(Long id);
    PagedResponse<ProductResponse> search(String keyword, String category, String brand,
                                           BigDecimal minPrice, BigDecimal maxPrice,
                                           BigDecimal minRating, Pageable pageable);
    List<ProductResponse> getFeatured();
    List<ProductResponse> getLatest();
    List<ProductResponse> getRelated(Long id);
}