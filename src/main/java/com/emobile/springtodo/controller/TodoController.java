package com.emobile.springtodo.controller;

import com.emobile.springtodo.dto.*;
import com.emobile.springtodo.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Slf4j
@RestController
@RequestMapping("/api/v1/todos")
@Validated
@RequiredArgsConstructor
public class TodoController implements TodoApi {
    private final TodoService todoService;

    @Override
    @PostMapping
    @ResponseStatus(CREATED)
    public TodoResponse create(@Valid @RequestBody CreateTodoRequest request) {
        log.info("HTTP POST /todos - create todo");
        return todoService.create(request);
    }

    @Override
    @GetMapping("/{id}")
    public TodoResponse getById(@PathVariable Long id) {
        log.debug("HTTP GET /todos/{} - fetch by id", id);
        return todoService.getById(id);
    }

    @Override
    @GetMapping
    public TodoListResponse getAll(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        log.debug("HTTP GET /todos - list (limit={}, offset={})", limit, offset);
        return todoService.getAll(limit, offset);
    }

    @Override
    @PutMapping("/{id}")
    public TodoResponse update(@PathVariable Long id, @Valid @RequestBody UpdateTodoRequest request) {
        log.info("HTTP PUT /todos/{} - update", id);
        return todoService.update(id, request);
    }

    @Override
    @PatchMapping("/{id}")
    public TodoResponse patch(@PathVariable Long id, @Valid @RequestBody PatchTodoRequest request) {
        log.info("HTTP PATCH /todos/{} - patch", id);
        return todoService.patch(id, request);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("HTTP DELETE /todos/{} - delete", id);
        todoService.delete(id);
    }
}