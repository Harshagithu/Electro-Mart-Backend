package com.electromart.util;

import com.electromart.entity.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PriceUtils {

    private PriceUtils() {}

    public static BigDecimal discountedPrice(Product product) {
        BigDecimal discount = product.getPrice()
                .multiply(product.getDiscountPercentage())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return product.getPrice().subtract(discount);
    }
}