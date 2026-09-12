package com.campuscrate.exception;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(Long itemId) {
        super("Item not found: " + itemId);
    }
}