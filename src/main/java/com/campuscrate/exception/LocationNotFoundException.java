package com.campuscrate.exception;

public class LocationNotFoundException extends RuntimeException {

    public LocationNotFoundException(Long locationId) {
        super("Location not found: " + locationId);
    }
}