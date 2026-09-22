package com.campuscrate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PasswordResetConfirmRequest(
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "\\d{6}", message = "Code must contain 6 digits") String code,
        @NotBlank @Size(min = 8, max = 100, message = "Password must be 8 to 100 characters") String password) {
}
