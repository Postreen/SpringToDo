package com.emobile.springtodo.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record ErrorResponse(
        String error,
        String message,
        List<String> details,
        OffsetDateTime timestamp
) {
}
