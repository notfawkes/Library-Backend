package com.library.exception;

import com.library.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle custom ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), 404),
                HttpStatus.NOT_FOUND
        );
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(false)
                .message("Validation failed")
                .data(errors) // Put validation errors in the data field
                .statusCode(400)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle registration conflicts (e.g., username exists)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        // Check message to distinguish between 400 and 401
        if ("Invalid username or password".equals(ex.getMessage())) {
            return new ResponseEntity<>(
                    ApiResponse.error(ex.getMessage(), 401),
                    HttpStatus.UNAUTHORIZED
            );
        }

        // Default to 400 Bad Request
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), 400),
                HttpStatus.BAD_REQUEST
        );
    }

    // Handle authentication failures (e.g., bad credentials)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(AuthenticationException ex) {
        return new ResponseEntity<>(
                ApiResponse.error("Authentication failed: " + ex.getMessage(), 401),
                HttpStatus.UNAUTHORIZED
        );
    }

    // Handle authorization failures (e.g., user is not admin)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>(
                ApiResponse.error("Access denied. You do not have permission to perform this action.", 403),
                HttpStatus.FORBIDDEN
        );
    }

    // Generic fallback handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception ex, WebRequest request) {
        // Log the exception
        ex.printStackTrace();

        return new ResponseEntity<>(
                ApiResponse.error("An unexpected error occurred: " + ex.getMessage(), 500),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}