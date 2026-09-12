package com.campuscrate.dto;

import java.time.LocalDate;

public record ItemResponse(
        Long itemId,
        String title,
        String description,
        String itemType,
        String imageUrl,
        String status,
        LocalDate createdAt,
        Long reportedBy,
        Long categoryId,
        Long locationId) {
}