package com.campuscrate.dto;

import java.time.LocalDateTime;

public record ShuttleDriverResponse(
        Long driverId,
        Long userId,
        String status,
        String phone,
        String vehicleName,
        String vehicleNumber,
        String profileInfo,
        Long approvedBy,
        LocalDateTime approvedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}