package com.campuscrate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LocationRequest(
        @NotBlank(message = "Location name is required")
        @Size(max = 100, message = "Location name must not exceed 100 characters")
        String name) {
}