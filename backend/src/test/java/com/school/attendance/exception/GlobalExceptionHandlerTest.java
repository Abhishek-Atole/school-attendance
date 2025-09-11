package com.school.attendance.exception;

import com.school.attendance.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    @Test
    void testHandleResourceNotFoundException() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("Student not found with id: 123");

        // When
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleResourceNotFoundException(exception, webRequest);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiResponse<Object> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.isSuccess());
        assertEquals("Student not found with id: 123", apiResponse.getMessage());
        assertEquals("/api/test", apiResponse.getPath());
    }

    @Test
    void testHandleBusinessException() {
        // Given
        BusinessException exception = new BusinessException("Invalid operation: Cannot delete student with active enrollment");

        // When
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleBusinessException(exception, webRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse<Object> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.isSuccess());
        assertEquals("Invalid operation: Cannot delete student with active enrollment", apiResponse.getMessage());
        assertEquals("/api/test", apiResponse.getPath());
    }

    @Test
    void testHandleNotificationException() {
        // Given
        NotificationException exception = new NotificationException("Failed to send email notification");

        // When
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleNotificationException(exception, webRequest);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ApiResponse<Object> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.isSuccess());
        assertEquals("Notification failed: Failed to send email notification", apiResponse.getMessage());
        assertEquals("/api/test", apiResponse.getPath());
    }

    @Test
    void testHandleIllegalArgumentException() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Invalid attendance status: UNKNOWN");

        // When
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleIllegalArgumentException(exception, webRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse<Object> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.isSuccess());
        assertEquals("Illegal argument: Invalid attendance status: UNKNOWN", apiResponse.getMessage());
        assertEquals("/api/test", apiResponse.getPath());
    }

    @Test
    void testHandleRuntimeException() {
        // Given
        RuntimeException exception = new RuntimeException("Unexpected error occurred");

        // When
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleRuntimeException(exception, webRequest);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ApiResponse<Object> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.isSuccess());
        assertEquals("Runtime exception: Unexpected error occurred", apiResponse.getMessage());
        assertEquals("/api/test", apiResponse.getPath());
    }

    @Test
    void testHandleGenericException() {
        // Given
        Exception exception = new Exception("Database connection failed");

        // When
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleGenericException(exception, webRequest);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ApiResponse<Object> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.isSuccess());
        assertEquals("Unexpected exception: Exception", apiResponse.getMessage());
        assertEquals("/api/test", apiResponse.getPath());
    }

    @Test
    void testHandleMethodArgumentNotValidException() {
        // Given
        FieldError fieldError1 = new FieldError("student", "name", "Name is required");
        FieldError fieldError2 = new FieldError("student", "email", "Invalid email format");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);

        // When
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleMethodArgumentNotValidException(methodArgumentNotValidException, webRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse<Object> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.isSuccess());
        assertEquals("Validation failed", apiResponse.getMessage());
        assertNotNull(apiResponse.getErrors());
        assertEquals(2, apiResponse.getErrors().size());
        assertTrue(apiResponse.getErrors().contains("name: Name is required"));
        assertTrue(apiResponse.getErrors().contains("email: Invalid email format"));
        assertEquals("/api/test", apiResponse.getPath());
    }
}
