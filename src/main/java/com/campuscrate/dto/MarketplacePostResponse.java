package com.campuscrate.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MarketplacePostResponse(
        Long postId,
        Long sellerId,
        Long categoryId,
        Long locationId,
        String title,
        String description,
        String condition,
        String sellingType,
        BigDecimal fixedPrice,
        BigDecimal startingPrice,
        LocalDateTime auctionStart,
        LocalDateTime auctionEnd,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}