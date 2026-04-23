package com.emobile.springtodo.repository;

import com.emobile.springtodo.model.Todo;

import java.util.List;
import java.util.Optional;

public interface TodoRepository {

    Todo create(String title, String description);

    Optional<Todo> findById(Long id);

    List<Todo> findAll(int limit, int offset);

    long countAll();

    Optional<Todo> update(Long id, String title, String description, boolean completed);

    boolean deleteById(Long id);

    long countCompleted();
}
