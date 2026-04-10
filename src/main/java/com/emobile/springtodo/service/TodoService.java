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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;

    @Transactional
    @Caching(
            put = @CachePut(value = "todoById", key = "#result.id"),
            evict = @CacheEvict(value = "todoList", allEntries = true)
    )
    public TodoResponse create(CreateTodoRequest request) {
        Todo todo = todoRepository.create(request.title(), request.description());
        return todoMapper.toResponse(todo);
    }

    @Cacheable(value = "todoById", key = "#id")
    public TodoResponse getById(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
        return todoMapper.toResponse(todo);
    }

    @Cacheable(value = "todoList", key = "{#limit, #offset}")
    public TodoListResponse getAll(int limit, int offset) {
        return new TodoListResponse(
                todoMapper.toResponseList(todoRepository.findAll(limit, offset)),
                limit,
                offset,
                todoRepository.countAll()
        );
    }

    @Transactional
    @Caching(
            put = @CachePut(value = "todoById", key = "#id"),
            evict = @CacheEvict(value = "todoList", allEntries = true)
    )
    public TodoResponse update(Long id, UpdateTodoRequest request) {
        Todo updated = todoRepository.update(
                id,
                request.title(),
                request.description(),
                request.completed()
        ).orElseThrow(() -> new TodoNotFoundException(id));

        return todoMapper.toResponse(updated);
    }

    @Transactional
    @Caching(
            put = @CachePut(value = "todoById", key = "#id"),
            evict = @CacheEvict(value = "todoList", allEntries = true)
    )
    public TodoResponse patch(Long id, PatchTodoRequest request) {
        validatePatchRequest(request);

        Todo existing = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));

        String title = request.title() != null ? request.title() : existing.title();
        String description = request.description() != null ? request.description() : existing.description();
        boolean completed = request.completed() != null ? request.completed() : existing.completed();

        Todo updated = todoRepository.update(id, title, description, completed)
                .orElseThrow(() -> new TodoNotFoundException(id));

        return todoMapper.toResponse(updated);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "todoById", key = "#id"),
                    @CacheEvict(value = "todoList", allEntries = true)
            }
    )
    public void delete(Long id) {
        boolean deleted = todoRepository.deleteById(id);
        if (!deleted) {
            throw new TodoNotFoundException(id);
        }
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