package com.campuscrate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminLoginRequest(
        @NotBlank(message = "Admin email is required") @Email(message = "Admin email must be valid") String email,
        @NotBlank(message = "Password is required") String password) {
}