package com.campuscrate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ShuttleTripRequest(
        @NotNull Long driverId,
        @NotBlank @Size(max = 50) String route,
        @NotNull Boolean startNow) {
}