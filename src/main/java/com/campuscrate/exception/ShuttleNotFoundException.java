package com.campuscrate.exception;

public class ShuttleNotFoundException extends RuntimeException {

    public ShuttleNotFoundException(String resource, Long id) {
        super("Shuttle " + resource + " not found: " + id);
    }
}
