package com.campuscrate.dto;

public record AuthResponse<T>(
        String accessToken,
        String tokenType,
        long expiresIn,
        T user) {
}
