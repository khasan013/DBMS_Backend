package com.campuscrate.model;

import java.time.LocalDate;

public class Claim {

    private Long claimId;
    private Long itemId;
    private Long claimantId;
    private String evidenceDescription;
    private String evidenceImgUrl;
    private String status;
    private Long adminId;
    private LocalDate updatedAt;

    public Claim() {
    }

    public Claim(Long claimId, Long itemId, Long claimantId, String evidenceDescription,
            String evidenceImgUrl, String status, Long adminId, LocalDate updatedAt) {
        this.claimId = claimId;
        this.itemId = itemId;
        this.claimantId = claimantId;
        this.evidenceDescription = evidenceDescription;
        this.evidenceImgUrl = evidenceImgUrl;
        this.status = status;
        this.adminId = adminId;
        this.updatedAt = updatedAt;
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getClaimantId() {
        return claimantId;
    }

    public void setClaimantId(Long claimantId) {
        this.claimantId = claimantId;
    }

    public String getEvidenceDescription() {
        return evidenceDescription;
    }

    public void setEvidenceDescription(String evidenceDescription) {
        this.evidenceDescription = evidenceDescription;
    }

    public String getEvidenceImgUrl() {
        return evidenceImgUrl;
    }

    public void setEvidenceImgUrl(String evidenceImgUrl) {
        this.evidenceImgUrl = evidenceImgUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
}