package com.school.attendance.util;

import com.school.attendance.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationUtil
 */
class ValidationUtilTest {

    @Test
    void testValidateEmail_ValidEmails() {
        assertDoesNotThrow(() -> ValidationUtil.validateEmail("test@example.com"));
        assertDoesNotThrow(() -> ValidationUtil.validateEmail("user.name@domain.org"));
        assertDoesNotThrow(() -> ValidationUtil.validateEmail("admin@school.edu"));
    }

    @Test
    void testValidateEmail_InvalidEmails() {
        assertThrows(BusinessException.class, () -> ValidationUtil.validateEmail(null));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateEmail(""));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateEmail("invalid-email"));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateEmail("@domain.com"));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateEmail("user@"));
    }

    @Test
    void testValidatePhoneNumber_ValidNumbers() {
        assertDoesNotThrow(() -> ValidationUtil.validatePhoneNumber("+1234567890"));
        assertDoesNotThrow(() -> ValidationUtil.validatePhoneNumber("1234567890"));
        assertDoesNotThrow(() -> ValidationUtil.validatePhoneNumber("+91 9876543210"));
    }

    @Test
    void testValidatePhoneNumber_InvalidNumbers() {
        assertThrows(BusinessException.class, () -> ValidationUtil.validatePhoneNumber(null));
        assertThrows(BusinessException.class, () -> ValidationUtil.validatePhoneNumber(""));
        assertThrows(BusinessException.class, () -> ValidationUtil.validatePhoneNumber("123"));
        assertThrows(BusinessException.class, () -> ValidationUtil.validatePhoneNumber("abcdefghij"));
    }

    @Test
    void testValidateRequired_ValidInputs() {
        assertDoesNotThrow(() -> ValidationUtil.validateRequired("valid input", "field"));
        assertDoesNotThrow(() -> ValidationUtil.validateRequired("  valid  ", "field"));
    }

    @Test
    void testValidateRequired_InvalidInputs() {
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> ValidationUtil.validateRequired(null, "field"));
        assertEquals("field is required", exception.getMessage());

        exception = assertThrows(BusinessException.class, 
            () -> ValidationUtil.validateRequired("", "field"));
        assertEquals("field is required", exception.getMessage());

        exception = assertThrows(BusinessException.class, 
            () -> ValidationUtil.validateRequired("   ", "field"));
        assertEquals("field is required", exception.getMessage());
    }

    @Test
    void testValidateLength_ValidInputs() {
        assertDoesNotThrow(() -> ValidationUtil.validateLength("test", "field", 2, 10));
        assertDoesNotThrow(() -> ValidationUtil.validateLength("hello world", "field", 5, 15));
        assertDoesNotThrow(() -> ValidationUtil.validateLength(null, "field", 2, 10)); // null is allowed
    }

    @Test
    void testValidateLength_InvalidInputs() {
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> ValidationUtil.validateLength("a", "field", 2, 10));
        assertEquals("field must be at least 2 characters", exception.getMessage());

        exception = assertThrows(BusinessException.class, 
            () -> ValidationUtil.validateLength("this is too long", "field", 2, 10));
        assertEquals("field must not exceed 10 characters", exception.getMessage());
    }

    @Test
    void testValidatePositive_ValidInputs() {
        assertDoesNotThrow(() -> ValidationUtil.validatePositive(1, "field"));
        assertDoesNotThrow(() -> ValidationUtil.validatePositive(1.5, "field"));
        assertDoesNotThrow(() -> ValidationUtil.validatePositive(100L, "field"));
    }

    @Test
    void testValidatePositive_InvalidInputs() {
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> ValidationUtil.validatePositive(null, "field"));
        assertEquals("field is required", exception.getMessage());

        exception = assertThrows(BusinessException.class, 
            () -> ValidationUtil.validatePositive(0, "field"));
        assertEquals("field must be positive", exception.getMessage());

        exception = assertThrows(BusinessException.class, 
            () -> ValidationUtil.validatePositive(-1, "field"));
        assertEquals("field must be positive", exception.getMessage());
    }

    @Test
    void testValidateId_ValidIds() {
        assertDoesNotThrow(() -> ValidationUtil.validateId(1L, "Student"));
        assertDoesNotThrow(() -> ValidationUtil.validateId(999L, "Student"));
    }

    @Test
    void testValidateId_InvalidIds() {
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> ValidationUtil.validateId(null, "Student"));
        assertEquals("Invalid Student ID: null", exception.getMessage());

        exception = assertThrows(BusinessException.class, 
            () -> ValidationUtil.validateId(0L, "Student"));
        assertEquals("Invalid Student ID: 0", exception.getMessage());

        exception = assertThrows(BusinessException.class, 
            () -> ValidationUtil.validateId(-1L, "Student"));
        assertEquals("Invalid Student ID: -1", exception.getMessage());
    }

    @Test
    void testValidateAndParseDate_ValidDates() {
        LocalDate result = ValidationUtil.validateAndParseDate("2023-12-01", "Date");
        assertEquals(LocalDate.of(2023, 12, 1), result);

        result = ValidationUtil.validateAndParseDate("2024-01-15", "Date");
        assertEquals(LocalDate.of(2024, 1, 15), result);
    }

    @Test
    void testValidateAndParseDate_InvalidDates() {
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> ValidationUtil.validateAndParseDate(null, "Date"));
        assertEquals("Date is required", exception.getMessage());

        exception = assertThrows(BusinessException.class, 
            () -> ValidationUtil.validateAndParseDate("", "Date"));
        assertEquals("Date is required", exception.getMessage());

        exception = assertThrows(BusinessException.class, 
            () -> ValidationUtil.validateAndParseDate("invalid-date", "Date"));
        assertTrue(exception.getMessage().contains("Invalid date format"));
    }

    @Test
    void testValidateDateRange_ValidRanges() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();
        
        assertDoesNotThrow(() -> ValidationUtil.validateDateRange(yesterday, today));
        assertDoesNotThrow(() -> ValidationUtil.validateDateRange(today, today));
    }

    @Test
    void testValidateDateRange_InvalidRanges() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> ValidationUtil.validateDateRange(null, today));
        assertEquals("Start date is required", exception.getMessage());

        exception = assertThrows(BusinessException.class, 
            () -> ValidationUtil.validateDateRange(today, null));
        assertEquals("End date is required", exception.getMessage());

        exception = assertThrows(BusinessException.class, 
            () -> ValidationUtil.validateDateRange(today, yesterday));
        assertEquals("Start date must be before or equal to end date", exception.getMessage());

        exception = assertThrows(BusinessException.class, 
            () -> ValidationUtil.validateDateRange(tomorrow, tomorrow));
        assertEquals("Start date cannot be in the future", exception.getMessage());
    }

    @Test
    void testValidateAttendancePercentage_ValidPercentages() {
        assertDoesNotThrow(() -> ValidationUtil.validateAttendancePercentage(0.0));
        assertDoesNotThrow(() -> ValidationUtil.validateAttendancePercentage(50.0));
        assertDoesNotThrow(() -> ValidationUtil.validateAttendancePercentage(100.0));
    }

    @Test
    void testValidateAttendancePercentage_InvalidPercentages() {
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> ValidationUtil.validateAttendancePercentage(null));
        assertEquals("Attendance percentage is required", exception.getMessage());

        exception = assertThrows(BusinessException.class, 
            () -> ValidationUtil.validateAttendancePercentage(-1.0));
        assertEquals("Attendance percentage must be between 0 and 100", exception.getMessage());

        exception = assertThrows(BusinessException.class, 
            () -> ValidationUtil.validateAttendancePercentage(101.0));
        assertEquals("Attendance percentage must be between 0 and 100", exception.getMessage());
    }

    @Test
    void testIsValidEmail_NonThrowingVersion() {
        // Valid emails
        assertTrue(ValidationUtil.isValidEmail("test@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user.name@domain.org"));
        assertTrue(ValidationUtil.isValidEmail("admin@school.edu"));

        // Invalid emails
        assertFalse(ValidationUtil.isValidEmail(null));
        assertFalse(ValidationUtil.isValidEmail(""));
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));
        assertFalse(ValidationUtil.isValidEmail("@domain.com"));
        assertFalse(ValidationUtil.isValidEmail("user@"));
    }

    @Test
    void testIsValidPhoneNumber_NonThrowingVersion() {
        // Valid phone numbers
        assertTrue(ValidationUtil.isValidPhoneNumber("+1234567890"));
        assertTrue(ValidationUtil.isValidPhoneNumber("1234567890"));
        assertTrue(ValidationUtil.isValidPhoneNumber("+91 9876-543-210"));

        // Invalid phone numbers
        assertFalse(ValidationUtil.isValidPhoneNumber(null));
        assertFalse(ValidationUtil.isValidPhoneNumber(""));
        assertFalse(ValidationUtil.isValidPhoneNumber("123"));
        assertFalse(ValidationUtil.isValidPhoneNumber("abcdefghij"));
    }
}
