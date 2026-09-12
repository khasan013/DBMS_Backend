package com.campuscrate.exception;

public class MarketplacePostNotFoundException extends RuntimeException {

    public MarketplacePostNotFoundException(Long postId) {
        super("Marketplace post not found: " + postId);
    }
}