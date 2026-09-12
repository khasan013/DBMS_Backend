package com.campuscrate.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MarketplacePostUpdateRequest(
        @NotNull(message = "Category ID is required") Long categoryId,
        @NotNull(message = "Location ID is required") Long locationId,
        @NotBlank(message = "Title is required") @Size(max = 255) String title,
        @Size(max = 5000) String description,
        @NotBlank(message = "Condition is required") @Size(max = 50) String condition,
        @NotBlank(message = "Selling type is required") @Size(max = 50) String sellingType,
        @DecimalMin(value = "0.01", message = "Fixed price must be greater than zero") BigDecimal fixedPrice,
        @DecimalMin(value = "0.01", message = "Starting price must be greater than zero") BigDecimal startingPrice,
        LocalDateTime auctionStart,
        LocalDateTime auctionEnd) {
}