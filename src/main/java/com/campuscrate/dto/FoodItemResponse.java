package com.campuscrate.dto;

import java.math.BigDecimal;
public record FoodItemResponse(Long foodItemId, Long vendorId, String name, String description, BigDecimal price,
        String imageUrl, boolean available) { }
