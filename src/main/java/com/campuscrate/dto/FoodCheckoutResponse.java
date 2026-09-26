package com.campuscrate.dto;

import java.math.BigDecimal;
public record FoodCheckoutResponse(Long orderId, BigDecimal totalAmount, String paymentMethod, String paymentStatus,
        String orderStatus, String gatewayUrl) { }
