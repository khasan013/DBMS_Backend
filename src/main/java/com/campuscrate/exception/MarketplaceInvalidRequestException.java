package com.campuscrate.exception;

public class MarketplaceInvalidRequestException extends RuntimeException {

    public MarketplaceInvalidRequestException(String message) {
        super(message);
    }
}