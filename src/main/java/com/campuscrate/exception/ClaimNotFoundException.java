package com.campuscrate.exception;

public class ClaimNotFoundException extends RuntimeException {

    public ClaimNotFoundException(Long claimId) {
        super("Claim not found: " + claimId);
    }
}