package com.campuscrate.model;

import java.time.LocalDate;

public class Item {

    private Long itemId;
    private String title;
    private String description;
    private String itemType;
    private String imageUrl;
    private String status;
    private LocalDate createdAt;
    private Long reportedBy;
    private Long categoryId;
    private Long locationId;

    public Item() {
    }

    public Item(Long itemId, String title, String description, String itemType,
            String imageUrl, String status, LocalDate createdAt, Long reportedBy,
            Long categoryId, Long locationId) {
        this.itemId = itemId;
        this.title = title;
        this.description = description;
        this.itemType = itemType;
        this.imageUrl = imageUrl;
        this.status = status;
        this.createdAt = createdAt;
        this.reportedBy = reportedBy;
        this.categoryId = categoryId;
        this.locationId = locationId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public Long getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(Long reportedBy) {
        this.reportedBy = reportedBy;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }
}