package com.school.attendance.exception;

import com.school.attendance.dto.ApiResponse;
import com.school.attendance.util.LoggingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for the School Attendance Management System
 * Handles all exceptions thrown by controllers and provides consistent error responses
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handle ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        String path = extractPath(request);
        LoggingUtil.logSecurityEvent("RESOURCE_NOT_FOUND", "unknown", 
                "Resource not found: " + ex.getMessage() + " | Path: " + path);
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), path);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handle BusinessException
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(
            BusinessException ex, WebRequest request) {
        
        String path = extractPath(request);
        LoggingUtil.logSecurityEvent("BUSINESS_EXCEPTION", "unknown", 
                "Business rule violation: " + ex.getMessage() + " | Path: " + path);
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), path);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle NotificationException
     */
    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotificationException(
            NotificationException ex, WebRequest request) {
        
        String path = extractPath(request);
        LoggingUtil.logSecurityEvent("NOTIFICATION_EXCEPTION", "unknown", 
                "Notification failed: " + ex.getMessage() + " | Path: " + path);
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), path);
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    /**
     * Handle validation errors from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        String path = extractPath(request);
        List<String> errors = new ArrayList<>();
        
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        
        LoggingUtil.logSecurityEvent("VALIDATION_ERROR", "unknown", 
                "Validation failed: " + errors + " | Path: " + path);
        
        ApiResponse<Object> response = ApiResponse.error("Validation failed", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle constraint validation errors
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        
        String path = extractPath(request);
        List<String> errors = new ArrayList<>();
        
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getMessage());
        }
        
        LoggingUtil.logSecurityEvent("CONSTRAINT_VIOLATION", "unknown", 
                "Constraint violations: " + errors + " | Path: " + path);
        
        ApiResponse<Object> response = ApiResponse.error("Validation failed", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        String path = extractPath(request);
        LoggingUtil.logSecurityEvent("ILLEGAL_ARGUMENT", "unknown", 
                "Illegal argument: " + ex.getMessage() + " | Path: " + path);
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), path);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        String path = extractPath(request);
        logger.error("Runtime exception occurred", ex);
        LoggingUtil.logSecurityEvent("RUNTIME_EXCEPTION", "unknown", 
                "Runtime exception: " + ex.getClass().getSimpleName() + " | Path: " + path);
        
        ApiResponse<Object> response = ApiResponse.error("An unexpected error occurred", path);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(
            Exception ex, WebRequest request) {
        
        String path = extractPath(request);
        logger.error("Unexpected exception occurred", ex);
        LoggingUtil.logSecurityEvent("GENERAL_EXCEPTION", "unknown", 
                "Unexpected exception: " + ex.getClass().getSimpleName() + " | Path: " + path);
        
        ApiResponse<Object> response = ApiResponse.error("An unexpected error occurred", path);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Extract request path from WebRequest description
     */
    private String extractPath(WebRequest request) {
        String description = request.getDescription(false);
        if (description.startsWith("uri=")) {
            return description.substring(4);
        }
        return description;
    }
}