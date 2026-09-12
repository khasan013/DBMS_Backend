package com.campuscrate.dto;

public record AdminResponse(
        Long adminId,
        String email,
        String phone,
        String profileImageUrl) {
}