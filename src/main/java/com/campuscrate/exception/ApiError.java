package com.campuscrate.exception;

public record ApiError(int status, String message) {
}