package com.campuscrate.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
public record VendorOrderResponse(Long orderId, String buyerName, String buyerPhone, String deliveryLocation,
        BigDecimal totalAmount, String paymentMethod, String paymentStatus, String orderStatus, LocalDateTime createdAt,
        List<VendorOrderItemResponse> items) { }
