package com.electromart.service.impl;

import com.electromart.dto.response.WishlistResponse;
import com.electromart.entity.Product;
import com.electromart.entity.Wishlist;
import com.electromart.entity.WishlistItem;
import com.electromart.entity.User;
import com.electromart.exception.ResourceNotFoundException;
import com.electromart.mapper.WishlistMapper;
import com.electromart.repository.ProductRepository;
import com.electromart.repository.WishlistItemRepository;
import com.electromart.repository.WishlistRepository;
import com.electromart.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public WishlistResponse getWishlist(User user) {
        return WishlistMapper.toResponse(getOwnedWishlist(user));
    }

    @Override
    @Transactional
    public WishlistResponse addItem(User user, Long productId) {
        Wishlist wishlist = getOwnedWishlist(user);

        // Idempotent by design: re-adding something already on the wishlist is a
        // no-op, not an error. Matches how a heart/wishlist icon actually behaves
        // in the UI — clicking an already-filled heart shouldn't throw a 409.
        if (!wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getId(), productId)) {
            Product product = productRepository.findById(productId)
                    .filter(Product::isActive)
                    .orElseThrow(() -> ResourceNotFoundException.of("Product", productId));

            WishlistItem item = WishlistItem.builder().wishlist(wishlist).product(product).build();
            wishlist.getItems().add(item);
            wishlistItemRepository.save(item);
        }

        return WishlistMapper.toResponse(getOwnedWishlist(user));
    }

    @Override
    @Transactional
    public WishlistResponse removeItem(User user, Long productId) {
        Wishlist wishlist = getOwnedWishlist(user);
        WishlistItem item = wishlist.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("This product isn't in your wishlist"));

        wishlist.getItems().remove(item);   // orphanRemoval = true deletes the row
        wishlistItemRepository.delete(item);

        return WishlistMapper.toResponse(getOwnedWishlist(user));
    }

    private Wishlist getOwnedWishlist(User user) {
        return wishlistRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found for this account"));
    }
}