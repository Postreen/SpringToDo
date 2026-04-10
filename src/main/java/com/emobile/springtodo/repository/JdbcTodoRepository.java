package com.emobile.springtodo.repository;

import com.emobile.springtodo.model.Todo;
import com.emobile.springtodo.repository.mapper.TodoRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcTodoRepository implements TodoRepository {

    private static final RowMapper<Todo> ROW_MAPPER = TodoRowMapper.INSTANCE;

    private static final String INSERT_SQL = """
            INSERT INTO todos(title, description, completed)
            VALUES (:title, :description, false)
            RETURNING id, title, description, completed, created_at, updated_at
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT id, title, description, completed, created_at, updated_at
            FROM todos
            WHERE id = :id
            """;

    private static final String FIND_ALL_SQL = """
            SELECT id, title, description, completed, created_at, updated_at
            FROM todos
            ORDER BY id
            LIMIT :limit OFFSET :offset
            """;

    private static final String COUNT_ALL_SQL = """
            SELECT COUNT(*) FROM todos
            """;

    private static final String UPDATE_SQL = """
            UPDATE todos
            SET title = :title,
                description = :description,
                completed = :completed,
                updated_at = now()
            WHERE id = :id
            RETURNING id, title, description, completed, created_at, updated_at
            """;

    private static final String DELETE_BY_ID_SQL = """
            DELETE FROM todos
            WHERE id = :id
            """;

    private static final String COUNT_COMPLETED_SQL = """
            SELECT COUNT(*) FROM todos
            WHERE completed = true
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Todo create(String title, String description) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", title)
                .addValue("description", description);

        return jdbcTemplate.queryForObject(INSERT_SQL, params, ROW_MAPPER);
    }

    @Override
    public Optional<Todo> findById(Long id) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        try {
            Todo todo = jdbcTemplate.queryForObject(FIND_BY_ID_SQL, params, ROW_MAPPER);
            return Optional.ofNullable(todo);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Todo> findAll(int limit, int offset) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("limit", limit)
                .addValue("offset", offset);

        return jdbcTemplate.query(FIND_ALL_SQL, params, ROW_MAPPER);
    }

    @Override
    public Optional<Todo> update(Long id, String title, String description, boolean completed) {
        List<Todo> todos = jdbcTemplate.query(
                UPDATE_SQL,
                todoParams(id, title, description, completed),
                ROW_MAPPER
        );
        return todos.stream().findFirst();
    }

    @Override
    public boolean deleteById(Long id) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        return jdbcTemplate.update(DELETE_BY_ID_SQL, params) > 0;
    }

    @Override
    public long countAll() {
        Long count = jdbcTemplate.queryForObject(COUNT_ALL_SQL, new MapSqlParameterSource(), Long.class);
        return count;
    }

    @Override
    public long countCompleted() {
        Long count = jdbcTemplate.queryForObject(COUNT_COMPLETED_SQL, new MapSqlParameterSource(), Long.class);
        return count;
    }

    private SqlParameterSource todoParams(Long id, String title, String description, boolean completed) {
        return new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("title", title)
                .addValue("description", description)
                .addValue("completed", completed);
    }
}
