package com.electromart.controller;

import com.electromart.dto.request.ReviewRequest;
import com.electromart.dto.response.PagedResponse;
import com.electromart.dto.response.ReviewResponse;
import com.electromart.security.CurrentUserProvider;
import com.electromart.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping("/api/reviews")
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewRequest request) {
        var review = reviewService.create(currentUserProvider.getCurrentUser(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @GetMapping("/api/products/{productId}/reviews")
    public ResponseEntity<PagedResponse<ReviewResponse>> getForProduct(
            @PathVariable Long productId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reviewService.getForProduct(productId, pageable));
    }
}