package com.campuscrate.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
public record FoodOrderLineRequest(@NotNull Long foodItemId, @NotNull @Min(1) Integer quantity) { }
