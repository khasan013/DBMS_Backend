package com.campuscrate.exception;

public class DuplicateLocationException extends RuntimeException {

    public DuplicateLocationException(String name) {
        super("Location already exists: " + name);
    }
}