package com.campuscrate.dto;

public record StatusHistoryResponse(
        Long historyId,
        Long itemId,
        Long claimId,
        String status) {
}