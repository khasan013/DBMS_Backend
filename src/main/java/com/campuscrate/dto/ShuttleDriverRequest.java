package com.campuscrate.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ShuttleDriverRequest(
        @NotNull Long userId,
        @Size(max = 32) String phone,
        @Size(max = 80) String vehicleName,
        @Size(max = 40) String vehicleNumber,
        @Size(max = 240) String profileInfo) {
}