package com.electromart.mapper;

import com.electromart.dto.response.WishlistItemResponse;
import com.electromart.dto.response.WishlistResponse;
import com.electromart.entity.Wishlist;
import com.electromart.entity.WishlistItem;
import com.electromart.util.PriceUtils;

public final class WishlistMapper {

    private WishlistMapper() {}

    public static WishlistItemResponse toItemResponse(WishlistItem item) {
        var product = item.getProduct();
        return WishlistItemResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .productImageUrl(product.getImageUrl())
                .price(product.getPrice())
                .discountedPrice(PriceUtils.discountedPrice(product))
                .inStock(product.getStockQuantity() != null && product.getStockQuantity() > 0)
                .addedAt(item.getAddedAt())
                .build();
    }

    public static WishlistResponse toResponse(Wishlist wishlist) {
        var items = wishlist.getItems().stream().map(WishlistMapper::toItemResponse).toList();
        return WishlistResponse.builder().items(items).totalItems(items.size()).build();
    }
}