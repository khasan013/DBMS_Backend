package com.campuscrate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StatusHistoryRequest(
        @NotNull(message = "Item ID is required") Long itemId,
        @NotNull(message = "Claim ID is required") Long claimId,
        @NotBlank(message = "Status is required")
        @Size(max = 50, message = "Status must not exceed 50 characters")
        String status) {
}