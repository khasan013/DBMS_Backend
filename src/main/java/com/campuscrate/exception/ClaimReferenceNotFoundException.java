package com.campuscrate.exception;

public class ClaimReferenceNotFoundException extends RuntimeException {

    public ClaimReferenceNotFoundException(String referenceType, Long referenceId) {
        super("Referenced " + referenceType + " not found: " + referenceId);
    }
}