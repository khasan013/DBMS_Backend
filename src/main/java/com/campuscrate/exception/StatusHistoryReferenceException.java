package com.campuscrate.exception;

public class StatusHistoryReferenceException extends RuntimeException {

    public StatusHistoryReferenceException(String referenceType, Long referenceId) {
        super("Referenced " + referenceType + " not found or does not match: " + referenceId);
    }
}