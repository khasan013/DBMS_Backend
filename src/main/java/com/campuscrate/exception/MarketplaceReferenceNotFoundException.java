package com.campuscrate.exception;

public class MarketplaceReferenceNotFoundException extends RuntimeException {

    public MarketplaceReferenceNotFoundException(String referenceType, Long referenceId) {
        super("Referenced " + referenceType + " not found: " + referenceId);
    }
}