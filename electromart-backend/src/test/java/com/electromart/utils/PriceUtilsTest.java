package com.electromart.utils;


import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.electromart.entity.Category;
import com.electromart.entity.Product;
import com.electromart.util.PriceUtils;


class PriceUtilsTest {

    private Product productWith(String price, String discountPercentage) {
        return Product.builder()
                .category(Category.builder().name("Test").build())
                .name("Test Product")
                .price(new BigDecimal(price))
                .discountPercentage(new BigDecimal(discountPercentage))
                .stockQuantity(10)
                .build();
    }

    @Test
    void noDiscountReturnsOriginalPrice() {
        Product product = productWith("1000.00", "0");
        assertThat(PriceUtils.discountedPrice(product)).isEqualByComparingTo("1000.00");
    }

    @Test
    void tenPercentDiscountIsAppliedCorrectly() {
        Product product = productWith("1000.00", "10");
        assertThat(PriceUtils.discountedPrice(product)).isEqualByComparingTo("900.00");
    }

    @Test
    void roundingHappensToTwoDecimalPlaces() {
        Product product = productWith("999.99", "33");
        // 999.99 * 0.33 = 329.9967 -> rounds to 330.00 -> 999.99 - 330.00 = 669.99
        assertThat(PriceUtils.discountedPrice(product)).isEqualByComparingTo("669.99");
    }
}