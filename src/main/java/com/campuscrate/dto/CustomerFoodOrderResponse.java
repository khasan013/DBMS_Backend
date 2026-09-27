package com.campuscrate.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CustomerFoodOrderResponse(Long orderId, String vendorName, String deliveryLocation,
        BigDecimal totalAmount, String paymentMethod, String paymentStatus, String orderStatus,
        LocalDateTime createdAt, List<VendorOrderItemResponse> items) { }
