package com.campuscrate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

public record UserRegistrationRequest(
        @NotBlank @Size(max = 50) String studentId,
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 8, max = 255) String password,
        @NotBlank @Pattern(regexp = "[0-9+()\\- ]{7,20}") String phone,
        @Size(max = 500) String profileImgUrl) {
}
