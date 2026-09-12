package com.campuscrate.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ShuttleTrip {

    private Long tripId;
    private Long driverId;
    private String route;
    private String status;
    private LocalDateTime scheduledStartAt;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime lastLocationAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ShuttleTrip() {
    }

    public ShuttleTrip(Long tripId, Long driverId, String route, String status, LocalDateTime scheduledStartAt,
            LocalDateTime startedAt, LocalDateTime endedAt, BigDecimal latitude, BigDecimal longitude,
            LocalDateTime lastLocationAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.tripId = tripId;
        this.driverId = driverId;
        this.route = route;
        this.status = status;
        this.scheduledStartAt = scheduledStartAt;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lastLocationAt = lastLocationAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }
    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }
    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getScheduledStartAt() { return scheduledStartAt; }
    public void setScheduledStartAt(LocalDateTime scheduledStartAt) { this.scheduledStartAt = scheduledStartAt; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public LocalDateTime getLastLocationAt() { return lastLocationAt; }
    public void setLastLocationAt(LocalDateTime lastLocationAt) { this.lastLocationAt = lastLocationAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}