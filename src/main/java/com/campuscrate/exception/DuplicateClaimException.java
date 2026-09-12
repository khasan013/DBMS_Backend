package com.campuscrate.exception;

public class DuplicateClaimException extends RuntimeException {

    public DuplicateClaimException(Long itemId) {
        super("A claim already exists for item: " + itemId);
    }
}