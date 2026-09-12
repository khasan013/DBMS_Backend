package com.campuscrate.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MarketplacePost {

    private Long postId;
    private Long sellerId;
    private Long categoryId;
    private Long locationId;
    private String title;
    private String description;
    private String condition;
    private String sellingType;
    private BigDecimal fixedPrice;
    private BigDecimal startingPrice;
    private LocalDateTime auctionStart;
    private LocalDateTime auctionEnd;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MarketplacePost() {
    }

    public MarketplacePost(Long postId, Long sellerId, Long categoryId, Long locationId,
            String title, String description, String condition, String sellingType,
            BigDecimal fixedPrice, BigDecimal startingPrice, LocalDateTime auctionStart,
            LocalDateTime auctionEnd, String status, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.postId = postId;
        this.sellerId = sellerId;
        this.categoryId = categoryId;
        this.locationId = locationId;
        this.title = title;
        this.description = description;
        this.condition = condition;
        this.sellingType = sellingType;
        this.fixedPrice = fixedPrice;
        this.startingPrice = startingPrice;
        this.auctionStart = auctionStart;
        this.auctionEnd = auctionEnd;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getSellingType() { return sellingType; }
    public void setSellingType(String sellingType) { this.sellingType = sellingType; }
    public BigDecimal getFixedPrice() { return fixedPrice; }
    public void setFixedPrice(BigDecimal fixedPrice) { this.fixedPrice = fixedPrice; }
    public BigDecimal getStartingPrice() { return startingPrice; }
    public void setStartingPrice(BigDecimal startingPrice) { this.startingPrice = startingPrice; }
    public LocalDateTime getAuctionStart() { return auctionStart; }
    public void setAuctionStart(LocalDateTime auctionStart) { this.auctionStart = auctionStart; }
    public LocalDateTime getAuctionEnd() { return auctionEnd; }
    public void setAuctionEnd(LocalDateTime auctionEnd) { this.auctionEnd = auctionEnd; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}