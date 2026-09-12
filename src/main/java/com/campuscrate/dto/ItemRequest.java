package com.campuscrate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ItemRequest(
        @NotBlank(message = "Item title is required")
        @Size(max = 255, message = "Item title must not exceed 255 characters")
        String title,
        @Size(max = 5000, message = "Item description must not exceed 5000 characters")
        String description,
        @NotBlank(message = "Item type is required")
        @Size(max = 100, message = "Item type must not exceed 100 characters")
        String itemType,
        @Size(max = 500, message = "Image URL must not exceed 500 characters")
        String imageUrl,
        @NotBlank(message = "Item status is required")
        @Size(max = 50, message = "Item status must not exceed 50 characters")
        String status,
        @NotNull(message = "Reported-by user ID is required") Long reportedBy,
        @NotNull(message = "Category ID is required") Long categoryId,
        @NotNull(message = "Location ID is required") Long locationId) {
}