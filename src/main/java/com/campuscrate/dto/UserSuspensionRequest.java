package com.campuscrate.dto;

import jakarta.validation.constraints.NotNull;

public record UserSuspensionRequest(@NotNull Boolean suspended) {
}
