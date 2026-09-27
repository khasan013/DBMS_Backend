package com.campuscrate.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MarketplaceSaleResponse(Long saleId, Long postId, Long buyerId, BigDecimal salePrice,
        String status, LocalDateTime soldAt) { }
