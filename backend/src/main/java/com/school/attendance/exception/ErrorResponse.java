package com.school.attendance.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response format for the School Attendance Management System
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    private boolean success;
    
    private String message;
    
    private Map<String, String> errors;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String path;
    
    // Static factory methods for creating error responses
    public static ErrorResponse of(boolean success, String message, LocalDateTime timestamp, String path) {
        ErrorResponse response = new ErrorResponse();
        response.success = success;
        response.message = message;
        response.timestamp = timestamp;
        response.path = path;
        return response;
    }
    
    public static ErrorResponse withErrors(boolean success, String message, Map<String, String> errors, 
                                          LocalDateTime timestamp, String path) {
        ErrorResponse response = new ErrorResponse();
        response.success = success;
        response.message = message;
        response.errors = errors;
        response.timestamp = timestamp;
        response.path = path;
        return response;
    }
}
