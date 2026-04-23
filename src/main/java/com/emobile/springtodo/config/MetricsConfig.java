package com.emobile.springtodo.config;

import com.emobile.springtodo.repository.TodoRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {
    @Bean
    public Gauge completedTodoGauge(TodoRepository todoRepository, MeterRegistry meterRegistry) {
        return Gauge.builder("todo.completed.count", todoRepository, TodoRepository::countCompleted)
                .description("Number of completed TODO tasks")
                .register(meterRegistry);
    }
}
