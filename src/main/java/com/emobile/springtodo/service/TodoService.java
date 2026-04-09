package com.emobile.springtodo.service;

import com.emobile.springtodo.dto.CreateTodoRequest;
import com.emobile.springtodo.dto.TodoListResponse;
import com.emobile.springtodo.dto.TodoResponse;
import com.emobile.springtodo.dto.UpdateTodoRequest;
import com.emobile.springtodo.exception.TodoNotFoundException;
import com.emobile.springtodo.mapper.TodoMapper;
import com.emobile.springtodo.model.Todo;
import com.emobile.springtodo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "todoById", key = "#id")
    public TodoResponse getById(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
        return todoMapper.toResponse(todo);
    }

    @Cacheable(value = "todoList", key = "#limit + '-' + #offset")
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
        Todo updated = todoRepository.update(id, request.title(), request.description(), request.completed());
        return todoMapper.toResponse(updated);
    }

    @CacheEvict(value = {"todoById", "todoList"}, allEntries = true)
    public void delete(Long id) {
        todoRepository.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
        todoRepository.deleteById(id);
    }
}