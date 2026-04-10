package com.emobile.springtodo.exception.model;

import java.time.Instant;
import java.util.List;

public record ErrorResponseDto(
        Instant timestamp,
        int status,
        ErrorCode code,
        String message,
        List<String> details,
        String path
) {
    public ErrorResponseDto(int status, ErrorCode code, String message, List<String> details, String path) {
        this(
                Instant.now(),
                status,
                code,
                message,
                details != null ? List.copyOf(details) : List.of(),
                path
        );
    }

    public ErrorResponseDto(int status, ErrorCode code, String message, String path) {
        this(
                Instant.now(),
                status,
                code,
                message,
                List.of(),
                path
        );
    }
}