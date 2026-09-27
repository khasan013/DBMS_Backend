package com.campuscrate.dto;

import jakarta.validation.constraints.NotNull;

public record VendorStoreStatusRequest(@NotNull Boolean online) { }
