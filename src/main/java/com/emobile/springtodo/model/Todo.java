package com.emobile.springtodo.model;

import java.time.OffsetDateTime;

public record Todo(
        Long id,
        String title,
        String description,
        boolean completed,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
