package com.campuscrate.exception;

public class DuplicateStudentIdException extends RuntimeException {

    public DuplicateStudentIdException(String studentId) {
        super("Student ID already exists: " + studentId);
    }
}