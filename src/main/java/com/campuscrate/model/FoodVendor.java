package com.campuscrate.model;
public record FoodVendor(Long vendorId, Long userId, String name, String location, String description, String phone,
        String imageUrl, boolean active) { }
