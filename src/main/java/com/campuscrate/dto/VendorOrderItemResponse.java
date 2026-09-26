package com.campuscrate.dto;
import java.math.BigDecimal;
public record VendorOrderItemResponse(String name, Integer quantity, BigDecimal unitPrice) { }
