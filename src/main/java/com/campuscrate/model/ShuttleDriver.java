package com.campuscrate.model;

import java.time.LocalDateTime;

public class ShuttleDriver {

    private Long driverId;
    private Long userId;
    private String status;
    private String phone;
    private String vehicleName;
    private String vehicleNumber;
    private String profileInfo;
    private Long approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ShuttleDriver() {
    }

    public ShuttleDriver(Long driverId, Long userId, String status, String phone, String vehicleName,
            String vehicleNumber, String profileInfo, Long approvedBy, LocalDateTime approvedAt,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.driverId = driverId;
        this.userId = userId;
        this.status = status;
        this.phone = phone;
        this.vehicleName = vehicleName;
        this.vehicleNumber = vehicleNumber;
        this.profileInfo = profileInfo;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getVehicleName() { return vehicleName; }
    public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public String getProfileInfo() { return profileInfo; }
    public void setProfileInfo(String profileInfo) { this.profileInfo = profileInfo; }
    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}