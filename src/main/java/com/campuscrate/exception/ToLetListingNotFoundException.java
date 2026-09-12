package com.campuscrate.exception;

public class ToLetListingNotFoundException extends RuntimeException {
    public ToLetListingNotFoundException(Long listingId) { super("To-let listing not found: " + listingId); }
}
