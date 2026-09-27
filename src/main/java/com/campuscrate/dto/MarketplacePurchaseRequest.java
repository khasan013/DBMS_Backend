package com.campuscrate.dto;

import jakarta.validation.constraints.NotNull;

/** A buyer confirms purchase of one fixed-price marketplace post. */
public record MarketplacePurchaseRequest(@NotNull Long buyerId) { }
