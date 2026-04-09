package com.emobile.springtodo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTodoRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 120, message = "Title length must be <= 120")
        String title,
        @Size(max = 500, message = "Description length must be <= 500")
        String description,
        @NotNull(message = "Completed flag is required")
        Boolean completed
) {
}
