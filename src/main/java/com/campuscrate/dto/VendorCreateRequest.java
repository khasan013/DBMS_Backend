package com.campuscrate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VendorCreateRequest(
        @NotBlank @Size(max = 64) String loginId,
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank @Size(max = 32) String phone,
        @NotBlank @Size(max = 150) String location,
        @Size(max = 2000) String description,
        @Size(max = 500) String imageUrl) { }
