package com.campuscrate.dto;

public record AdminResponse(
        Long adminId,
        String name,
        String email,
        String phone,
        String profileImageUrl) {
}
