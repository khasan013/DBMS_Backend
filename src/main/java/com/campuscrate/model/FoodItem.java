package com.campuscrate.model;
import java.math.BigDecimal;
public record FoodItem(Long foodItemId, Long vendorId, String name, String description, BigDecimal price, String imageUrl,
        boolean available) { }
