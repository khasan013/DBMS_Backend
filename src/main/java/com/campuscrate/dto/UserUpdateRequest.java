package com.campuscrate.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(max = 100) String name,
        @Pattern(regexp = "[0-9+()\\- ]{7,20}") String phone,
        @Size(max = 500) String profileImgUrl) {
}