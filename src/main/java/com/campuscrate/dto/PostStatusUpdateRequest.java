package com.campuscrate.dto;

import jakarta.validation.constraints.NotBlank;

public record PostStatusUpdateRequest(@NotBlank String status) {
}
