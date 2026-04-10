package com.emobile.springtodo.controller;

import com.emobile.springtodo.dto.CreateTodoRequest;
import com.emobile.springtodo.dto.TodoListResponse;
import com.emobile.springtodo.dto.TodoResponse;
import com.emobile.springtodo.dto.UpdateTodoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "TODO API", description = "Operations for TODO tasks")
public interface TodoApi {

    @Operation(summary = "Create TODO")
    @ApiResponse(responseCode = "201", description = "Created")
    TodoResponse create(@Valid @RequestBody CreateTodoRequest request);

    @Operation(summary = "Get TODO by id")
    @ApiResponse(responseCode = "200", description = "Found")
    TodoResponse getById(@PathVariable Long id);

    @Operation(summary = "Get TODO list with limit-offset pagination")
    TodoListResponse getAll(
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
            @RequestParam(defaultValue = "0") @Min(0) int offset
    );

    @Operation(summary = "Update TODO")
    TodoResponse update(@PathVariable Long id, @Valid @RequestBody UpdateTodoRequest request);

    @Operation(summary = "Delete TODO")
    void delete(@PathVariable Long id);
}
