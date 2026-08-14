package com.electromart.mapper;

import com.electromart.dto.response.ReviewResponse;
import com.electromart.entity.Review;

public final class ReviewMapper {

    private ReviewMapper() {}

    public static ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                // First name + last-initial rather than full name — a small
                // privacy courtesy on a public-facing review, same idea as
                // "J. Smith" on any storefront's review section.
                .reviewerName(review.getUser().getFirstName() + " " + review.getUser().getLastName().charAt(0) + ".")
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}