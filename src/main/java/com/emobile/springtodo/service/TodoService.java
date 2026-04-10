package com.emobile.springtodo.service;

import com.emobile.springtodo.dto.*;
import com.emobile.springtodo.exception.InvalidPatchRequestException;
import com.emobile.springtodo.exception.TodoNotFoundException;
import com.emobile.springtodo.mapper.TodoMapper;
import com.emobile.springtodo.model.Todo;
import com.emobile.springtodo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
        TodoResponse response = todoMapper.toResponse(todo);

        log.info("Created todo with id={}", response.id());
        return response;
    }

    @Cacheable(value = "todoById", key = "#id")
    public TodoResponse getById(Long id) {
        log.debug("Fetching todo by id={}", id);

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Todo with id={} not found", id);
                    return new TodoNotFoundException(id);
                });

        return todoMapper.toResponse(todo);
    }

    @Cacheable(value = "todoList", key = "{#limit, #offset}")
    public TodoListResponse getAll(int limit, int offset) {
        log.debug("Fetching todo list with limit={} and offset={}", limit, offset);

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
        ).orElseThrow(() -> {
            log.warn("Failed to update todo: id={} not found", id);
            return new TodoNotFoundException(id);
        });

        TodoResponse response = todoMapper.toResponse(updated);
        log.info("Updated todo with id={}", id);
        return response;
    }

    @Transactional
    @Caching(
            put = @CachePut(value = "todoById", key = "#id"),
            evict = @CacheEvict(value = "todoList", allEntries = true)
    )
    public TodoResponse patch(Long id, PatchTodoRequest request) {
        validatePatchRequest(request);

        Todo existing = todoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Failed to patch todo: id={} not found", id);
                    return new TodoNotFoundException(id);
                });

        String title = request.title() != null ? request.title() : existing.title();
        String description = request.description() != null ? request.description() : existing.description();
        boolean completed = request.completed() != null ? request.completed() : existing.completed();

        Todo updated = todoRepository.update(id, title, description, completed)
                .orElseThrow(() -> {
                    log.warn("Failed to patch todo during update stage: id={} not found", id);
                    return new TodoNotFoundException(id);
                });

        TodoResponse response = todoMapper.toResponse(updated);
        log.info("Patched todo with id={}", id);
        return response;
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
            log.warn("Failed to delete todo: id={} not found", id);
            throw new TodoNotFoundException(id);
        }

        log.info("Deleted todo with id={}", id);
    }

    private void validatePatchRequest(PatchTodoRequest request) {
        if (request.title() == null &&
                request.description() == null &&
                request.completed() == null) {
            log.warn("Patch request is invalid: all fields are null");
            throw new InvalidPatchRequestException();
        }
    }
}