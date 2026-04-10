package com.emobile.springtodo.controller;

import com.emobile.springtodo.dto.CreateTodoRequest;
import com.emobile.springtodo.dto.TodoListResponse;
import com.emobile.springtodo.dto.TodoResponse;
import com.emobile.springtodo.dto.UpdateTodoRequest;
import com.emobile.springtodo.service.TodoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
        log.info("Creating TODO: {}", request);
        return todoService.create(request);
    }

    @Override
    @GetMapping("/{id}")
    public TodoResponse getById(@PathVariable Long id) {
        log.info("Fetching TODO with id: {}", id);
        return todoService.getById(id);
    }

    @Override
    @GetMapping
    public TodoListResponse getAll(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        log.info("Fetching TODO list with limit {} and offset {}", limit, offset);
        return todoService.getAll(limit, offset);
    }

    @Override
    @PutMapping("/{id}")
    public TodoResponse update(@PathVariable Long id, @Valid @RequestBody UpdateTodoRequest request) {
        log.info("Updating TODO id {} with data {}", id, request);
        return todoService.update(id, request);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Deleting TODO with id {}", id);
        todoService.delete(id);
    }
}