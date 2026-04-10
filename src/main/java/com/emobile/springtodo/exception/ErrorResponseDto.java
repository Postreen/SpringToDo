package com.emobile.springtodo.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "Error response DTO")
public record ErrorResponseDto(
        @Schema(description = "Timestamp of the error", example = "2026-04-10T12:34:56.789Z")
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant timestamp,

        @Schema(description = "HTTP status code", example = "404")
        int status,

        @Schema(description = "Error type", example = "NOT_FOUND")
        String error,

        @Schema(description = "Error message", example = "Todo not found")
        String message,

        @Schema(description = "Optional details", example = "[\"field1: must not be blank\"]")
        List<String> details,

        @Schema(description = "Request path", example = "/api/v1/todos/1")
        String path
) {
    public ErrorResponseDto(int status, String error, String message, List<String> details, String path) {
        this(Instant.now(), status, error, message, details, path);
    }
}