package com.campuscrate.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ToLetListingResponse(Long listingId, Long ownerId, String title, String description, String area,
        BigDecimal monthlyRent, int bedrooms, int bathrooms, String contactPhone, LocalDate availableFrom,
        String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
