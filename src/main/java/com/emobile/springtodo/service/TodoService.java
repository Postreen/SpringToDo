package com.emobile.springtodo.service;

import com.emobile.springtodo.dto.*;
import com.emobile.springtodo.exception.InvalidPatchRequestException;
import com.emobile.springtodo.exception.TodoNotFoundException;
import com.emobile.springtodo.mapper.TodoMapper;
import com.emobile.springtodo.model.Todo;
import com.emobile.springtodo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;

    @CacheEvict(value = "todoList", allEntries = true)
    public TodoResponse create(CreateTodoRequest request) {
        Todo todo = todoRepository.create(request.title(), request.description());
        return todoMapper.toResponse(todo);
    }

    @org.springframework.cache.annotation.Cacheable(value = "todoById", key = "#id")
    public TodoResponse getById(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
        return todoMapper.toResponse(todo);
    }

    @org.springframework.cache.annotation.Cacheable(value = "todoList", key = "#limit + '-' + #offset")
    public TodoListResponse getAll(int limit, int offset) {
        return new TodoListResponse(
                todoMapper.toResponseList(todoRepository.findAll(limit, offset)),
                limit,
                offset,
                todoRepository.countAll()
        );
    }

    @CachePut(value = "todoById", key = "#id")
    @CacheEvict(value = "todoList", allEntries = true)
    public TodoResponse update(Long id, UpdateTodoRequest request) {
        todoRepository.findById(id).orElseThrow(() -> new TodoNotFoundException(id));

        Todo updated = todoRepository.update(
                id,
                request.title(),
                request.description(),
                request.completed()
        );

        return todoMapper.toResponse(updated);
    }

    @CachePut(value = "todoById", key = "#id")
    @CacheEvict(value = "todoList", allEntries = true)
    public TodoResponse patch(Long id, PatchTodoRequest request) {
        validatePatchRequest(request);

        Todo existing = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));

        String title = request.title() != null ? request.title() : existing.title();
        String description = request.description() != null ? request.description() : existing.description();
        boolean completed = request.completed() != null ? request.completed() : existing.completed();

        Todo updated = todoRepository.update(id, title, description, completed);
        return todoMapper.toResponse(updated);
    }

    @Caching(evict = {
            @CacheEvict(value = "todoById", key = "#id"),
            @CacheEvict(value = "todoList", allEntries = true)
    })
    public void delete(Long id) {
        todoRepository.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
        todoRepository.deleteById(id);
    }

    private void validatePatchRequest(PatchTodoRequest request) {
        if (request.title() == null &&
                request.description() == null &&
                request.completed() == null
        ) {
            throw new InvalidPatchRequestException();
        }
    }
}