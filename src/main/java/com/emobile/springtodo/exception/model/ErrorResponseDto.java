package com.emobile.springtodo.exception.model;

import java.time.Instant;
import java.util.List;

public record ErrorResponseDto(
        Instant timestamp,
        int status,
        ErrorCode error,
        String message,
        List<String> details,
        String path
) {
    public ErrorResponseDto(int status, ErrorCode error, String message, List<String> details, String path) {
        this(Instant.now(), status, error, message, details, path);
    }
}