package com.campuscrate.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.URL;

public record ToLetListingRequest(
        @NotNull Long ownerId,
        @NotBlank @Size(max = 255) String title,
        @NotBlank @Size(max = 5000) String description,
        @NotBlank @Size(max = 150) String area,
        @NotNull @DecimalMin("0.01") BigDecimal monthlyRent,
        @Min(0) @Max(20) int bedrooms,
        @Min(0) @Max(20) int bathrooms,
        @NotBlank @Size(max = 32) String contactPhone,
        LocalDate availableFrom,
        @Size(max = 3, message = "A to-let listing can include at most 3 photos") List<@URL String> photoUrls) {
}
