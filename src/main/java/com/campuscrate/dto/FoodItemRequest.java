package com.campuscrate.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FoodItemRequest(@NotBlank @Size(max = 150) String name, @Size(max = 3000) String description,
        @NotNull @DecimalMin(value = "0.01") BigDecimal price, @Size(max = 500) String imageUrl, Boolean available) { }
