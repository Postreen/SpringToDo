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
        this(
                Instant.now(),
                status,
                error,
                message,
                details != null ? List.copyOf(details) : List.of(),
                path
        );
    }
    public ErrorResponseDto(int status, ErrorCode error, String message, String path) {
        this(
                Instant.now(),
                status,
                error,
                message,
                List.of(),
                path
        );
    }
}