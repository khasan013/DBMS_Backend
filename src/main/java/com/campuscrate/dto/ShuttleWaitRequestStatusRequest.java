package com.campuscrate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ShuttleWaitRequestStatusRequest(
        @NotNull Long driverId,
        @NotBlank String status) {
}
