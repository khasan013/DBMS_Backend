package com.campuscrate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ClaimStatusUpdateRequest(
        @NotBlank(message = "Claim status is required")
        @Size(max = 50, message = "Claim status must not exceed 50 characters")
        String status,
        @NotNull(message = "Admin ID is required") Long adminId) {
}