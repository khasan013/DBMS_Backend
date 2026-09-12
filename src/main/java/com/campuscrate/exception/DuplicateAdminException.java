package com.campuscrate.exception;

public class DuplicateAdminException extends RuntimeException {

    public DuplicateAdminException(String fields) {
        super("Admin value already exists for: " + fields);
    }
}