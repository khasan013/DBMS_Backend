package com.campuscrate.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShuttleTripResponse(
        Long tripId,
        Long driverId,
        String route,
        String status,
        LocalDateTime scheduledStartAt,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        BigDecimal latitude,
        BigDecimal longitude,
        LocalDateTime lastLocationAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}