package com.school.attendance.util;

import com.school.attendance.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Validation utility class for common validation operations
 */
public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[1-9]\\d{7,14}$"
    );
    
    /**
     * Validate email format
     */
    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BusinessException("Email address is required");
        }
        
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new BusinessException("Invalid email format: " + email);
        }
    }
    
    /**
     * Validate phone number format
     */
    public static void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new BusinessException("Phone number is required");
        }
        
        String cleanNumber = phoneNumber.replaceAll("[\\s\\-\\(\\)]", "");
        if (!PHONE_PATTERN.matcher(cleanNumber).matches()) {
            throw new BusinessException("Invalid phone number format: " + phoneNumber);
        }
    }
    
    /**
     * Validate required string
     */
    public static void validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(fieldName + " is required");
        }
    }
    
    /**
     * Validate string length
     */
    public static void validateLength(String value, String fieldName, int minLength, int maxLength) {
        if (value == null) {
            return;
        }
        
        int length = value.trim().length();
        if (length < minLength) {
            throw new BusinessException(fieldName + " must be at least " + minLength + " characters");
        }
        
        if (length > maxLength) {
            throw new BusinessException(fieldName + " must not exceed " + maxLength + " characters");
        }
    }
    
    /**
     * Validate positive number
     */
    public static void validatePositive(Number value, String fieldName) {
        if (value == null) {
            throw new BusinessException(fieldName + " is required");
        }
        
        if (value.doubleValue() <= 0) {
            throw new BusinessException(fieldName + " must be positive");
        }
    }
    
    /**
     * Validate ID (must be positive)
     */
    public static void validateId(Long id, String entityName) {
        if (id == null || id <= 0) {
            throw new BusinessException("Invalid " + entityName + " ID: " + id, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Validate date format
     */
    public static LocalDate validateAndParseDate(String dateStr, String fieldName) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new BusinessException(fieldName + " is required");
        }
        
        try {
            return LocalDate.parse(dateStr.trim());
        } catch (DateTimeParseException e) {
            throw new BusinessException("Invalid date format for " + fieldName + ": " + dateStr + 
                    ". Expected format: YYYY-MM-DD");
        }
    }
    
    /**
     * Validate date range
     */
    public static void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new BusinessException("Start date is required");
        }
        
        if (endDate == null) {
            throw new BusinessException("End date is required");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Start date must be before or equal to end date");
        }
        
        if (startDate.isAfter(LocalDate.now())) {
            throw new BusinessException("Start date cannot be in the future");
        }
    }
    
    /**
     * Validate attendance percentage
     */
    public static void validateAttendancePercentage(Double percentage) {
        if (percentage == null) {
            throw new BusinessException("Attendance percentage is required");
        }
        
        if (percentage < 0 || percentage > 100) {
            throw new BusinessException("Attendance percentage must be between 0 and 100");
        }
    }
    
    /**
     * Check if email format is valid (non-throwing version)
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Check if phone number format is valid (non-throwing version)
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }
        String cleanNumber = phoneNumber.replaceAll("[\\s\\-\\(\\)]", "");
        return PHONE_PATTERN.matcher(cleanNumber).matches();
    }
}
