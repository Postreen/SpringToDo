package com.emobile.springtodo.repository.mapper;

import com.emobile.springtodo.model.Todo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public final class TodoRowMapper implements RowMapper<Todo> {

    public static final TodoRowMapper INSTANCE = new TodoRowMapper();

    private TodoRowMapper() {
    }

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
