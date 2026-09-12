package com.campuscrate.model;

public class StatusHistory {

    private Long historyId;
    private Long itemId;
    private Long claimId;
    private String status;

    public StatusHistory() {
    }

    public StatusHistory(Long historyId, Long itemId, Long claimId, String status) {
        this.historyId = historyId;
        this.itemId = itemId;
        this.claimId = claimId;
        this.status = status;
    }

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}