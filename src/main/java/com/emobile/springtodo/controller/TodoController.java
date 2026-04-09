package com.emobile.springtodo.controller;

import com.emobile.springtodo.dto.CreateTodoRequest;
import com.emobile.springtodo.dto.TodoListResponse;
import com.emobile.springtodo.dto.TodoResponse;
import com.emobile.springtodo.dto.UpdateTodoRequest;
import com.emobile.springtodo.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

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
        return todoService.create(request);
    }

    @Override
    @GetMapping("/{id}")
    public TodoResponse getById(@PathVariable Long id) {
        return todoService.getById(id);
    }

    @Override
    @GetMapping
    public TodoListResponse getAll(int limit, int offset) {
        return todoService.getAll(limit, offset);
    }

    @Override
    @PutMapping("/{id}")
    public TodoResponse update(@PathVariable Long id, @Valid @RequestBody UpdateTodoRequest request) {
        return todoService.update(id, request);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Long id) {
        todoService.delete(id);
    }
}
