package com.campuscrate.dto;

import jakarta.validation.constraints.NotBlank;

public record FoodOrderStatusRequest(@NotBlank String status) { }
