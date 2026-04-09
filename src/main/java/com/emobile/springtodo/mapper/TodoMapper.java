package com.emobile.springtodo.mapper;

import com.emobile.springtodo.dto.TodoResponse;
import com.emobile.springtodo.model.Todo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TodoMapper {

    TodoResponse toResponse(Todo todo);

    List<TodoResponse> toResponseList(List<Todo> todos);
}
