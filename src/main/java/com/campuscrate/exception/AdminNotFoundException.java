package com.campuscrate.exception;

public class AdminNotFoundException extends RuntimeException {

    public AdminNotFoundException(Long adminId) {
        super("Admin not found: " + adminId);
    }
}