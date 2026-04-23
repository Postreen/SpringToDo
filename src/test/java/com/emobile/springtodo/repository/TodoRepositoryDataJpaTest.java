package com.emobile.springtodo.repository;

import com.emobile.springtodo.model.Todo;
import com.emobile.springtodo.support.ContainersConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JdbcTodoRepository.class)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TodoRepositoryDataJpaTest extends ContainersConfig {

    @Autowired
    private TodoRepository todoRepository;

    @Test
    @DisplayName("Repository creates and reads todo using JdbcTemplate with isolated DB")
    void createAndRead() {
        Todo created = todoRepository.create("repo title", "repo description");

        Optional<Todo> found = todoRepository.findById(created.id());

        assertTrue(found.isPresent());
        assertEquals("repo title", found.get().title());
        assertEquals(1, todoRepository.countAll());
    }

    @Test
    @DisplayName("Repository counts completed todos correctly")
    @Sql(scripts = "/sql/todos_seed.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void countCompleted() {
        assertEquals(1, todoRepository.countCompleted());
    }
}