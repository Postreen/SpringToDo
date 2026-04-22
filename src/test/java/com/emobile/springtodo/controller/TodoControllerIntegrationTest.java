package com.emobile.springtodo.controller;

import com.emobile.springtodo.support.ContainersConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TodoControllerIntegrationTest extends ContainersConfig {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/v1/todos creates todo and returns JSON")
    void createTodo() throws Exception {
        String body = """
                {
                  "title": "Write tests",
                  "description": "Controller integration test"
                }
                """;

        mockMvc.perform(post("/api/v1/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Write tests"))
                .andExpect(jsonPath("$.description").value("Controller integration test"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("GET /api/v1/todos/{id} returns todo by id")
    @Sql(scripts = "/sql/todos_seed.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getTodoById() throws Exception {
        mockMvc.perform(get("/api/v1/todos/{id}", 100))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.title").value("seed one"))
                .andExpect(jsonPath("$.description").value("first seeded"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("GET /api/v1/todos returns paginated list")
    @Sql(scripts = "/sql/todos_seed.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getTodosWithPagination() throws Exception {
        mockMvc.perform(get("/api/v1/todos")
                        .param("limit", "1")
                        .param("offset", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].id").value(101))
                .andExpect(jsonPath("$.items[0].title").value("seed two"))
                .andExpect(jsonPath("$.items[0].description").value("second seeded"))
                .andExpect(jsonPath("$.items[0].completed").value(true))
                .andExpect(jsonPath("$.items[0].createdAt").exists())
                .andExpect(jsonPath("$.items[0].updatedAt").exists())
                .andExpect(jsonPath("$.limit").value(1))
                .andExpect(jsonPath("$.offset").value(1))
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.total").value(2));
    }

    @Test
    @DisplayName("PUT /api/v1/todos/{id} updates todo")
    @Sql(scripts = "/sql/todos_seed.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateTodo() throws Exception {
        String body = """
                {
                  "title": "updated title",
                  "description": "updated description",
                  "completed": true
                }
                """;

        mockMvc.perform(put("/api/v1/todos/{id}", 100)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.title").value("updated title"))
                .andExpect(jsonPath("$.description").value("updated description"))
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("PATCH /api/v1/todos/{id} partially updates todo")
    @Sql(scripts = "/sql/todos_seed.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void patchTodo() throws Exception {
        String body = """
                {
                  "description": "patched description"
                }
                """;

        mockMvc.perform(patch("/api/v1/todos/{id}", 101)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(101))
                .andExpect(jsonPath("$.title").value("seed two"))
                .andExpect(jsonPath("$.description").value("patched description"))
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("DELETE /api/v1/todos/{id} removes todo")
    @Sql(scripts = "/sql/todos_seed.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteTodo() throws Exception {
        mockMvc.perform(delete("/api/v1/todos/{id}", 100))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/todos/{id}", 100))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/api/v1/todos/100"));
    }

    @Test
    @DisplayName("GlobalExceptionHandler returns validation error for invalid payload")
    void invalidPayloadValidationError() throws Exception {
        String body = """
                {
                  "title": ""
                }
                """;

        mockMvc.perform(post("/api/v1/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.path").value("/api/v1/todos"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("GlobalExceptionHandler returns not found for missing todo")
    void missingTodoError() throws Exception {
        mockMvc.perform(get("/api/v1/todos/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/api/v1/todos/9999"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("GlobalExceptionHandler returns bad request for empty patch")
    @Sql(scripts = "/sql/todos_seed.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void emptyPatchError() throws Exception {
        mockMvc.perform(patch("/api/v1/todos/{id}", 100)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("At least one field must be provided for patch"))
                .andExpect(jsonPath("$.path").value("/api/v1/todos/100"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}