package com.campuscrate.dto;

import java.time.LocalDate;

public record ClaimResponse(
        Long claimId,
        Long itemId,
        Long claimantId,
        String evidenceDescription,
        String evidenceImgUrl,
        String status,
        Long adminId,
        LocalDate updatedAt) {
}