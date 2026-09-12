package com.campuscrate.exception;

public class ItemReferenceNotFoundException extends RuntimeException {

    public ItemReferenceNotFoundException(String referenceType, Long referenceId) {
        super("Referenced " + referenceType + " not found: " + referenceId);
    }
}