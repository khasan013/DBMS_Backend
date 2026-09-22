package com.campuscrate.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RecentHighlightResponse(String highlightId, String module, String title, String description,
        String status, BigDecimal price, String imageUrl, LocalDateTime createdAt, String categoryOrArea) {
}
