package com.example.featureflag.controller;

import com.example.featureflag.exception.FlagAlreadyExistsException;
import com.example.featureflag.exception.FlagNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(FlagNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFlagNotFoundException(FlagNotFoundException ex) {
        log.warn("Flag not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(FlagAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleFlagAlreadyExistsException(FlagAlreadyExistsException ex) {
        log.warn("Flag already exists: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ValidationErrorResponse error = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                Instant.now(),
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    @Data
    @AllArgsConstructor
    static class ErrorResponse {
        private int status;
        private String message;
        private Instant timestamp;
    }
    
    @Data
    @AllArgsConstructor
    static class ValidationErrorResponse {
        private int status;
        private String message;
        private Instant timestamp;
        private Map<String, String> errors;
    }
}
