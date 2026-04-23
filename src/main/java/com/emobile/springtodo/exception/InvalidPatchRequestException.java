package com.emobile.springtodo.exception;

public class InvalidPatchRequestException extends RuntimeException {

    public InvalidPatchRequestException() {
        super("At least one field must be provided for patch");
    }
}
