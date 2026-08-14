package com.electromart.service;

import com.electromart.dto.request.ReviewRequest;
import com.electromart.dto.response.PagedResponse;
import com.electromart.dto.response.ReviewResponse;
import com.electromart.entity.User;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponse create(User user, ReviewRequest request);
    PagedResponse<ReviewResponse> getForProduct(Long productId, Pageable pageable);
    void adminDelete(Long reviewId);
}