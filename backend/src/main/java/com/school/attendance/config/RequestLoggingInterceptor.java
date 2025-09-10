package com.school.attendance.config;

import com.school.attendance.util.LoggingUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor for logging HTTP requests and responses
 */
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    private static final String START_TIME_ATTRIBUTE = "startTime";
    
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);
        
        // Set logging context
        String userId = getCurrentUserId(request);
        LoggingUtil.setRequestContext(request, userId);
        
        // Log request details
        logger.info("Incoming request: {} {} from IP: {} | User-Agent: {}", 
                request.getMethod(), 
                request.getRequestURI(),
                LoggingUtil.getClientIpAddress(request),
                request.getHeader("User-Agent"));
        
        return true;
    }
    
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, 
                              @NonNull Object handler, @Nullable Exception ex) {
        
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime != null) {
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Log response details
            logger.info("Request completed: {} {} | Status: {} | Duration: {}ms", 
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    executionTime);
            
            // Log performance if request took too long
            if (executionTime > 5000) { // 5 seconds threshold
                LoggingUtil.logPerformance(
                    request.getMethod() + " " + request.getRequestURI(),
                    executionTime,
                    "Slow request detected"
                );
            }
            
            // Log error if exception occurred
            if (ex != null) {
                logger.error("Request failed: {} {} | Error: {}", 
                        request.getMethod(),
                        request.getRequestURI(),
                        ex.getMessage(), ex);
            }
        }
        
        // Clear logging context
        LoggingUtil.clearContext();
    }
    
    /**
     * Get current user ID from request
     * This would typically come from JWT token or session
     */
    private String getCurrentUserId(HttpServletRequest request) {
        // For now, return the basic auth username if available
        if (request.getUserPrincipal() != null) {
            return request.getUserPrincipal().getName();
        }
        return "anonymous";
    }
}
