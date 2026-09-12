package com.campuscrate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ClaimRequest(
        @NotNull(message = "Item ID is required") Long itemId,
        @NotNull(message = "Claimant ID is required") Long claimantId,
        @NotBlank(message = "Evidence description is required")
        @Size(max = 5000, message = "Evidence description must not exceed 5000 characters")
        String evidenceDescription,
        @Size(max = 500, message = "Evidence image URL must not exceed 500 characters")
        String evidenceImgUrl,
        @NotBlank(message = "Claim status is required")
        @Size(max = 50, message = "Claim status must not exceed 50 characters")
        String status) {
}