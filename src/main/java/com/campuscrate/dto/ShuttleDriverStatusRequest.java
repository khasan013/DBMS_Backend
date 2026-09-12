package com.campuscrate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ShuttleDriverStatusRequest(
        @NotBlank String status,
        @NotNull Long approvedByUserId) {
}
