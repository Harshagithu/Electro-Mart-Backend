package com.electromart.service.impl;

import com.electromart.dto.request.ReviewRequest;
import com.electromart.dto.response.PagedResponse;
import com.electromart.dto.response.ReviewResponse;
import com.electromart.entity.OrderItem;
import com.electromart.entity.Product;
import com.electromart.entity.Review;
import com.electromart.entity.User;
import com.electromart.enums.OrderStatus;
import com.electromart.exception.DuplicateResourceException;
import com.electromart.exception.ForbiddenException;
import com.electromart.exception.InvalidOrderStateException;
import com.electromart.exception.ResourceNotFoundException;
import com.electromart.mapper.ReviewMapper;
import com.electromart.repository.OrderItemRepository;
import com.electromart.repository.ProductRepository;
import com.electromart.repository.ReviewRepository;
import com.electromart.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ReviewResponse create(User user, ReviewRequest request) {
        OrderItem orderItem = orderItemRepository.findById(request.getOrderItemId())
                .orElseThrow(() -> ResourceNotFoundException.of("Order item", request.getOrderItemId()));

        if (!orderItem.getOrder().getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You can only review products from your own orders");
        }
        if (orderItem.getOrder().getStatus() != OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException("You can only review a product after your order has been delivered");
        }
        if (reviewRepository.existsByOrderItemId(orderItem.getId())) {
            throw new DuplicateResourceException("You've already reviewed this purchase");
        }
        Product product = orderItem.getProduct();
        if (product == null) {
            throw new ResourceNotFoundException("This product is no longer available for review");
        }

        Review review = Review.builder()
                .product(product)
                .user(user)
                .orderItem(orderItem)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        review = reviewRepository.save(review);

        applyRatingIncrement(product, request.getRating());

        return ReviewMapper.toResponse(review);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReviewResponse> getForProduct(Long productId, Pageable pageable) {
        Page<Review> page = reviewRepository.findByProductId(productId, pageable);
        return PagedResponse.from(page.map(ReviewMapper::toResponse));
    }

    @Override
    @Transactional
    public void adminDelete(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> ResourceNotFoundException.of("Review", reviewId));
        Product product = review.getProduct();
        reviewRepository.delete(review);
        recomputeRatingFromScratch(product);
    }

    // Incremental update on create — cheap, no full table scan needed for the
    // common case (one new review at a time).
    private void applyRatingIncrement(Product product, int newRating) {
        BigDecimal oldTotal = product.getRating().multiply(BigDecimal.valueOf(product.getReviewCount()));
        BigDecimal newTotal = oldTotal.add(BigDecimal.valueOf(newRating));
        int newCount = product.getReviewCount() + 1;

        product.setRating(newTotal.divide(BigDecimal.valueOf(newCount), 2, RoundingMode.HALF_UP));
        product.setReviewCount(newCount);
        productRepository.save(product);
    }

    // Full recompute on delete — deletions are rare (admin moderation only),
    // so a fresh AVG() query is worth it to guarantee correctness rather than
    // trying to reverse the incremental formula and risk drift over time.
    private void recomputeRatingFromScratch(Product product) {
        long count = reviewRepository.countByProductId(product.getId());
        Double avg = reviewRepository.averageRatingForProduct(product.getId());

        product.setReviewCount((int) count);
        product.setRating(count == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
        productRepository.save(product);
    }
}