package com.emobile.springtodo.service;

import com.emobile.springtodo.dto.CreateTodoRequest;
import com.emobile.springtodo.dto.PatchTodoRequest;
import com.emobile.springtodo.dto.TodoResponse;
import com.emobile.springtodo.dto.UpdateTodoRequest;
import com.emobile.springtodo.exception.InvalidPatchRequestException;
import com.emobile.springtodo.exception.TodoNotFoundException;
import com.emobile.springtodo.mapper.TodoMapper;
import com.emobile.springtodo.model.Todo;
import com.emobile.springtodo.repository.TodoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private TodoMapper todoMapper;

    @InjectMocks
    private TodoService todoService;

    @Test
    @DisplayName("create delegates to repository and mapper")
    void createSuccess() {
        CreateTodoRequest request = new CreateTodoRequest("title", "description");
        Todo todo = todo(1L, "title", "description");
        TodoResponse expected = response(1L, "title", "description");

        when(todoRepository.create("title", "description")).thenReturn(todo);
        when(todoMapper.toResponse(todo)).thenReturn(expected);

        TodoResponse result = todoService.create(request);

        assertEquals(expected, result);
        verify(todoRepository).create("title", "description");
        verify(todoMapper).toResponse(todo);
    }

    @Test
    @DisplayName("getById throws TodoNotFoundException when todo not found")
    void getByIdNotFound() {
        when(todoRepository.findById(77L)).thenReturn(Optional.empty());

        assertThrows(TodoNotFoundException.class, () -> todoService.getById(77L));
    }

    @Test
    @DisplayName("patch throws InvalidPatchRequestException when all fields are null")
    void patchInvalidRequest() {
        PatchTodoRequest request = new PatchTodoRequest(null, null, null);

        assertThrows(InvalidPatchRequestException.class, () -> todoService.patch(1L, request));
        verify(todoRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("patch merges existing and new values")
    void patchMergesValues() {
        Todo existing = todo(5L, "old title", "old description");
        Todo updated = todo(5L, "old title", "new description");
        TodoResponse mapped = response(5L, "old title", "new description");

        when(todoRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(todoRepository.update(5L, "old title", "new description", false)).thenReturn(Optional.of(updated));
        when(todoMapper.toResponse(updated)).thenReturn(mapped);

        TodoResponse result = todoService.patch(5L, new PatchTodoRequest(null, "new description", null));

        assertEquals(mapped, result);
        verify(todoRepository).update(5L, "old title", "new description", false);
    }

    @Test
    @DisplayName("update throws TodoNotFoundException when repository returns empty")
    void updateNotFound() {
        UpdateTodoRequest request = new UpdateTodoRequest("title", "description", true);
        when(todoRepository.update(10L, "title", "description", true)).thenReturn(Optional.empty());

        assertThrows(TodoNotFoundException.class, () -> todoService.update(10L, request));
    }

    private Todo todo(Long id, String title, String description) {
        return new Todo(id, title, description, false, OffsetDateTime.now(), OffsetDateTime.now());
    }

    private TodoResponse response(Long id, String title, String description) {
        return new TodoResponse(id, title, description, false, OffsetDateTime.now(), OffsetDateTime.now());
    }
}