package com.emobile.springtodo.exception;

import com.emobile.springtodo.exception.model.ErrorCode;
import com.emobile.springtodo.exception.model.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TodoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFound(TodoNotFoundException ex, HttpServletRequest request) {
        log.warn("Todo not found: path={}, message={}", request.getRequestURI(), ex.getMessage());

        return new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                ErrorCode.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();

        log.warn("Request validation failed: path={}, errors={}", request.getRequestURI(), details);

        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.VALIDATION_ERROR,
                "Request validation failed",
                details,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InvalidPatchRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleInvalidPatch(
            InvalidPatchRequestException ex,
            HttpServletRequest request
    ) {
        log.warn("Invalid patch request: path={}, message={}", request.getRequestURI(), ex.getMessage());

        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleMalformedJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Malformed request body: path={}", request.getRequestURI());

        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.BAD_REQUEST,
                "Malformed request body",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleOther(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: path={}", request.getRequestURI(), ex);

        return new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ErrorCode.INTERNAL_ERROR,
                "Internal server error",
                request.getRequestURI()
        );
    }
}