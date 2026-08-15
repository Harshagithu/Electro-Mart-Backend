package com.electromart.service;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.electromart.dto.request.AddCartItemRequest;
import com.electromart.entity.Cart;
import com.electromart.entity.Category;
import com.electromart.entity.Product;
import com.electromart.entity.User;
import com.electromart.exception.InsufficientStockException;
import com.electromart.repository.CartItemRepository;
import com.electromart.repository.CartRepository;
import com.electromart.repository.ProductRepository;
import com.electromart.service.impl.CartServiceImpl;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    private CartServiceImpl cartService;

    private User user;

    private Cart cart;

    @BeforeEach
    void setUp() {

        cartService = new CartServiceImpl(
                cartRepository,
                cartItemRepository,
                productRepository
        );

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .build();

        cart = Cart.builder()
                .id(10L)
                .user(user)
                .items(new ArrayList<>())
                .build();

        lenient()
                .when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));
    }

    @Test
    void addingMoreThanAvailableStockIsRejected() {

        Product lowStockProduct = Product.builder()
                .id(5L)
                .category(
                        Category.builder()
                                .name("Test")
                                .build()
                )
                .name("Rare Item")
                .price(new BigDecimal("500"))
                .discountPercentage(BigDecimal.ZERO)
                .stockQuantity(3)
                .active(true)
                .build();

        when(productRepository.findById(5L))
                .thenReturn(Optional.of(lowStockProduct));

        when(cartItemRepository.findByCartIdAndProductId(10L, 5L))
                .thenReturn(Optional.empty());

        AddCartItemRequest request = new AddCartItemRequest();

        request.setProductId(5L);
        request.setQuantity(10);

        assertThatThrownBy(
                () -> cartService.addItem(user, request)
        )
        .isInstanceOf(InsufficientStockException.class);
    }
}