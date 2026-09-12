package com.campuscrate.dto;

public record UserResponse(
        Long userId,
        String studentId,
        String name,
        String email,
        boolean emailVerified,
        String phone,
        String profileImgUrl) {
}
