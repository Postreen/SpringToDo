package com.emobile.springtodo.repository;

import com.emobile.springtodo.model.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcTodoRepository implements TodoRepository {

    private static final RowMapper<Todo> ROW_MAPPER = new TodoRowMapper();

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Todo create(String title, String description) {
        String sql = """
                INSERT INTO todos(title, description, completed)
                VALUES (:title, :description, false)
                RETURNING id, title, description, completed, created_at, updated_at
                """;
        return jdbcTemplate.queryForObject(
                sql,
                Map.of("title", title, "description", description),
                ROW_MAPPER
        );
    }

    @Override
    public Optional<Todo> findById(Long id) {
        String sql = """
                SELECT id, title, description, completed, created_at, updated_at
                FROM todos
                WHERE id = :id
                """;
        List<Todo> rows = jdbcTemplate.query(sql, Map.of("id", id), ROW_MAPPER);
        return rows.stream().findFirst();
    }

    @Override
    public List<Todo> findAll(int limit, int offset) {
        String sql = """
                SELECT id, title, description, completed, created_at, updated_at
                FROM todos
                ORDER BY id
                LIMIT :limit OFFSET :offset
                """;
        return jdbcTemplate.query(
                sql,
                Map.of("limit", limit, "offset", offset),
                ROW_MAPPER
        );
    }

    @Override
    public long countAll() {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM todos",
                Map.of(),
                Long.class
        );
    }

    @Override
    public Todo update(Long id, String title, String description, boolean completed) {
        String sql = """
                UPDATE todos
                SET title = :title,
                    description = :description,
                    completed = :completed,
                    updated_at = now()
                WHERE id = :id
                RETURNING id, title, description, completed, created_at, updated_at
                """;
        return jdbcTemplate.queryForObject(sql,
                new MapSqlParameterSource()
                        .addValue("id", id)
                        .addValue("title", title)
                        .addValue("description", description)
                        .addValue("completed", completed),
                ROW_MAPPER);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(
                "DELETE FROM todos WHERE id = :id",
                Map.of("id", id)
        );
    }

    @Override
    public long countCompleted() {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM todos WHERE completed = true",
                Map.of(),
                Long.class
        );
    }

    private static class TodoRowMapper implements RowMapper<Todo> {
        @Override
        public Todo mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Todo(
                    rs.getLong("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getBoolean("completed"),
                    rs.getObject("created_at", OffsetDateTime.class),
                    rs.getObject("updated_at", OffsetDateTime.class)
            );
        }
    }
}
