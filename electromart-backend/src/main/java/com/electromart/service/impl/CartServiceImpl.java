package com.electromart.service.impl;

import com.electromart.dto.request.AddCartItemRequest;
import com.electromart.dto.request.UpdateCartItemRequest;
import com.electromart.dto.response.CartResponse;
import com.electromart.entity.Cart;
import com.electromart.entity.CartItem;
import com.electromart.entity.Product;
import com.electromart.entity.User;
import com.electromart.exception.ForbiddenException;
import com.electromart.exception.InsufficientStockException;
import com.electromart.exception.ResourceNotFoundException;
import com.electromart.mapper.CartMapper;
import com.electromart.repository.CartItemRepository;
import com.electromart.repository.CartRepository;
import com.electromart.repository.ProductRepository;
import com.electromart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(User user) {
        return CartMapper.toResponse(getOwnedCart(user));
    }

    @Override
    @Transactional
    public CartResponse addItem(User user, AddCartItemRequest request) {
        Cart cart = getOwnedCart(user);
        Product product = productRepository.findById(request.getProductId())
                .filter(Product::isActive)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", request.getProductId()));

        var existing = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        int desiredQuantity = existing.map(i -> i.getQuantity() + request.getQuantity()).orElse(request.getQuantity());
        validateStock(product, desiredQuantity);

        if (existing.isPresent()) {
            existing.get().setQuantity(desiredQuantity);
            cartItemRepository.save(existing.get());
        } else {
            CartItem item = CartItem.builder().cart(cart).product(product).quantity(desiredQuantity).build();
            cart.getItems().add(item); // keeps the in-memory collection consistent for the mapper below
            cartItemRepository.save(item);
        }

        return CartMapper.toResponse(getOwnedCart(user));
    }

    @Override
    @Transactional
    public CartResponse updateQuantity(User user, Long itemId, UpdateCartItemRequest request) {
        Cart cart = getOwnedCart(user);
        CartItem item = findOwnedItem(cart, itemId);

        validateStock(item.getProduct(), request.getQuantity());
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return CartMapper.toResponse(getOwnedCart(user));
    }

    @Override
    @Transactional
    public CartResponse removeItem(User user, Long itemId) {
        Cart cart = getOwnedCart(user);
        CartItem item = findOwnedItem(cart, itemId);
        cart.getItems().remove(item);       // orphanRemoval = true on Cart.items deletes the row
        cartItemRepository.delete(item);

        return CartMapper.toResponse(getOwnedCart(user));
    }

    @Override
    @Transactional
    public void clearCart(User user) {
        Cart cart = getOwnedCart(user);
        cart.getItems().clear();            // orphanRemoval handles the DELETEs
    }

    private void validateStock(Product product, int desiredQuantity) {
        if (desiredQuantity > product.getStockQuantity()) {
            throw new InsufficientStockException(
                    "Only " + product.getStockQuantity() + " unit(s) of \"" + product.getName() + "\" available");
        }
    }

    private Cart getOwnedCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for this account"));
    }

    private CartItem findOwnedItem(Cart cart, Long itemId) {
        return cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> ResourceNotFoundException.of("Cart item", itemId));
    }
}