package com.tasktracker.task.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик ошибок для всего приложения
 * 
 * Этот класс обрабатывает все исключения, которые могут возникнуть
 * в контроллерах, и возвращает клиенту структурированные ответы об ошибках.
 * 
 * Важные принципы:
 * 1. Централизованная обработка ошибок
 * 2. Единый формат ответов об ошибках
 * 3. Логирование для отладки
 * 4. Безопасность - не раскрываем внутренние детали
 * 
 * @author Orazbakhov Aibek
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Обработка ошибок валидации
     * 
     * Срабатывает когда @Valid аннотация находит ошибки в данных,
     * например, пустые обязательные поля или неверный формат данных.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        logger.warn("Validation error occurred: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                "Переданные данные не прошли валидацию",
                request.getDescription(false),
                errors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Обработка ошибок "Не найдено"
     * 
     * Срабатывает когда запрашиваемый ресурс не найден в базе данных.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        logger.error("Runtime exception occurred: {}", ex.getMessage(), ex);
        
        // Определяем тип ошибки по сообщению
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String title = "Internal Server Error";
        String message = "Произошла внутренняя ошибка сервера";
        
        if (ex.getMessage().contains("not found")) {
            status = HttpStatus.NOT_FOUND;
            title = "Resource Not Found";
            message = ex.getMessage();
        } else if (ex.getMessage().contains("Access denied")) {
            status = HttpStatus.FORBIDDEN;
            title = "Access Denied";
            message = ex.getMessage();
        } else if (ex.getMessage().contains("already exists")) {
            status = HttpStatus.CONFLICT;
            title = "Resource Already Exists";
            message = ex.getMessage();
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                title,
                message,
                request.getDescription(false),
                null
        );
        
        return ResponseEntity.status(status).body(errorResponse);
    }
    
    /**
     * Обработка всех остальных исключений
     * 
     * Catch-all обработчик для неожиданных ошибок.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        
        logger.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Произошла непредвиденная ошибка сервера",
                request.getDescription(false),
                null
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Класс для структурированного ответа об ошибке
     * 
     * Этот класс обеспечивает единый формат всех ошибок в API.
     * Клиенты всегда получают ошибки в одном и том же формате.
     */
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        private Map<String, String> validationErrors;
        
        public ErrorResponse(int status, String error, String message, String path, Map<String, String> validationErrors) {
            this.timestamp = LocalDateTime.now();
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
            this.validationErrors = validationErrors;
        }
        
        // Getters and Setters
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        
        public Map<String, String> getValidationErrors() { return validationErrors; }
        public void setValidationErrors(Map<String, String> validationErrors) { this.validationErrors = validationErrors; }
    }
} 