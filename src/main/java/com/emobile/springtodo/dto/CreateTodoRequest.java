package com.emobile.springtodo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTodoRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 120, message = "Title length must be <= 120")
        String title,
        @Size(max = 500, message = "Description length must be <= 500")
        String description
) {
}
