package com.aiinterviewcoach.exception;

import com.aiinterviewcoach.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> validation(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return response(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "The request contains invalid values.",
                request,
                fieldErrors);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    ResponseEntity<ApiErrorResponse> passwordMismatch(
            PasswordMismatchException exception, HttpServletRequest request) {
        return response(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "The request contains invalid values.",
                request,
                Map.of("confirmPassword", exception.getMessage()));
    }

    @ExceptionHandler(InvalidInterviewConfigurationException.class)
    ResponseEntity<ApiErrorResponse> invalidInterviewConfiguration(
            InvalidInterviewConfigurationException exception, HttpServletRequest request) {
        return response(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "The interview configuration is invalid.",
                request,
                Map.of(exception.getField(), exception.getMessage()));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    ResponseEntity<ApiErrorResponse> duplicateEmail(
            DuplicateEmailException exception, HttpServletRequest request) {
        return response(HttpStatus.CONFLICT, "Conflict", exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    ResponseEntity<ApiErrorResponse> invalidCredentials(
            InvalidCredentialsException exception, HttpServletRequest request) {
        return response(HttpStatus.UNAUTHORIZED, "Unauthorized", exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiErrorResponse> notFound(
            ResourceNotFoundException exception, HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, "Not Found", exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiErrorResponse> dataConflict(
            DataIntegrityViolationException exception, HttpServletRequest request) {
        log.warn("Database constraint violation at {}", request.getRequestURI());
        return response(
                HttpStatus.CONFLICT,
                "Conflict",
                "The requested operation conflicts with existing data.",
                request,
                Map.of());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> unexpected(Exception exception, HttpServletRequest request) {
        log.error("Unexpected error at {}", request.getRequestURI(), exception);
        return response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred.",
                request,
                Map.of());
    }

    private static ResponseEntity<ApiErrorResponse> response(
            HttpStatus status,
            String error,
            String message,
            HttpServletRequest request,
            Map<String, String> fieldErrors) {
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(), status.value(), error, message, request.getRequestURI(), fieldErrors);
        return ResponseEntity.status(status).body(body);
    }
}
