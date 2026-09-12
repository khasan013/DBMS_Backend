package com.campuscrate.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShuttleWaitRequestResponse(
        Long waitRequestId,
        Long tripId,
        Long driverId,
        Long userId,
        BigDecimal latitude,
        BigDecimal longitude,
        String status,
        LocalDateTime decisionAt,
        LocalDateTime insideAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}