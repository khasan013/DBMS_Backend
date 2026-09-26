package com.campuscrate.dto;

import java.util.List;
public record VendorResponse(Long vendorId, Long userId, String name, String location, String description, String phone,
        String imageUrl, boolean active, List<FoodItemResponse> foodItems) { }
