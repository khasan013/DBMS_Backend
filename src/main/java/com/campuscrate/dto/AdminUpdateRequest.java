package com.campuscrate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AdminUpdateRequest(
        @Email(message = "Admin email must be valid") @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,
        @Pattern(regexp = "[0-9+()\\- ]{7,20}", message = "Phone number is invalid")
        String phone,
        @Size(max = 500, message = "Profile image URL must not exceed 500 characters")
        String profileImageUrl) {
}