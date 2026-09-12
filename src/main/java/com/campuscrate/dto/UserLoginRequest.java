package com.campuscrate.dto;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(
        @NotBlank String studentId,
        @NotBlank String password) {
}