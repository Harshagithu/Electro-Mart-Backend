package com.electromart.service.impl;

import com.electromart.dto.request.ProductRequest;
import com.electromart.dto.response.PagedResponse;
import com.electromart.dto.response.ProductResponse;
import com.electromart.entity.Category;
import com.electromart.entity.Product;
import com.electromart.exception.ResourceNotFoundException;
import com.electromart.mapper.ProductMapper;
import com.electromart.repository.CategoryRepository;
import com.electromart.repository.ProductRepository;
import com.electromart.service.ProductService;
import com.electromart.service.spec.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public ProductResponse create(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> ResourceNotFoundException.of("Category", request.getCategoryId()));

        Product product = Product.builder()
                .category(category)
                .name(request.getName())
                .description(request.getDescription())
                .brand(request.getBrand())
                .price(request.getPrice())
                .discountPercentage(request.getDiscountPercentage())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(request.getImageUrl())
                .rating(BigDecimal.ZERO)
                .reviewCount(0)
                .featured(request.isFeatured())
                .active(true)
                .build();

        return ProductMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", id));

        if (!product.getCategory().getId().equals(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Category", request.getCategoryId()));
            product.setCategory(category);
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setDiscountPercentage(request.getDiscountPercentage());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());
        product.setFeatured(request.isFeatured());
        // rating/reviewCount are NOT touched here — those are owned by the
        // Review module (Phase 12), never hand-edited through a product update.

        return ProductMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", id));
        // Soft delete again, same reasoning as Category: a hard delete would
        // cascade into order_items/cart_items/reviews history. Deactivating
        // removes it from storefront browsing without erasing past orders
        // that reference it.
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return ProductMapper.toResponse(productRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> search(String keyword, String category, String brand,
                                                   BigDecimal minPrice, BigDecimal maxPrice,
                                                   BigDecimal minRating, Pageable pageable) {
        Page<Product> page = productRepository.findAll(
                ProductSpecifications.build(keyword, category, brand, minPrice, maxPrice, minRating, true),
                pageable);
        return PagedResponse.from(page.map(ProductMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getFeatured() {
        return productRepository.findTop8ByFeaturedTrueAndActiveTrue().stream()
                .map(ProductMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getLatest() {
        return productRepository.findTop8ByActiveTrueOrderByCreatedAtDesc().stream()
                .map(ProductMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getRelated(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", id));
        return productRepository
                .findTop8ByCategoryIdAndActiveTrueAndIdNotOrderByRatingDesc(product.getCategory().getId(), id)
                .stream().map(ProductMapper::toResponse).toList();
    }
}