package com.campuscrate.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ShuttleWaitRequest {

    private Long waitRequestId;
    private Long tripId;
    private Long driverId;
    private Long userId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String status;
    private LocalDateTime decisionAt;
    private LocalDateTime insideAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ShuttleWaitRequest() {
    }

    public ShuttleWaitRequest(Long waitRequestId, Long tripId, Long driverId, Long userId,
            BigDecimal latitude, BigDecimal longitude, String status, LocalDateTime decisionAt,
            LocalDateTime insideAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.waitRequestId = waitRequestId;
        this.tripId = tripId;
        this.driverId = driverId;
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.decisionAt = decisionAt;
        this.insideAt = insideAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getWaitRequestId() { return waitRequestId; }
    public void setWaitRequestId(Long waitRequestId) { this.waitRequestId = waitRequestId; }
    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }
    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getDecisionAt() { return decisionAt; }
    public void setDecisionAt(LocalDateTime decisionAt) { this.decisionAt = decisionAt; }
    public LocalDateTime getInsideAt() { return insideAt; }
    public void setInsideAt(LocalDateTime insideAt) { this.insideAt = insideAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}