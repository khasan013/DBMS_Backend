package com.campuscrate.dto;

public record AdminUserResponse(Long userId, String studentId, String name, String email,
        boolean emailVerified, String phone, String profileImgUrl, boolean suspended) {
}
