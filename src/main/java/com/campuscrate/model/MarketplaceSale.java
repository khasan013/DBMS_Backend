package com.campuscrate.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MarketplaceSale(Long saleId, Long postId, Long buyerId, BigDecimal salePrice, String status,
        LocalDateTime soldAt) { }
