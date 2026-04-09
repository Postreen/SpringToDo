package com.emobile.springtodo.dto;

import java.util.List;

public record TodoListResponse(
        List<TodoResponse> items,
        int limit,
        int offset,
        long total
) {
}
