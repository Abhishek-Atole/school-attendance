package com.school.attendance.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:noreply@school.com}")
    private String fromEmail;
    
    @Value("${notification.email.enabled:false}")
    private boolean emailEnabled;

    /**
     * Send email using Spring Mail
     */
    public boolean sendEmail(String to, String subject, String body) {
        try {
            // If email is disabled or mail sender is not configured, use mock
            if (!emailEnabled || mailSender == null) {
                return sendMockEmail(to, subject, body);
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            
            mailSender.send(message);
            logger.info("Email sent successfully to: {}", to);
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    /**
     * Mock email sending for development/testing
     */
    private boolean sendMockEmail(String to, String subject, String body) {
        logger.info("MOCK EMAIL SENT:");
        logger.info("To: {}", to);
        logger.info("Subject: {}", subject);
        logger.info("Body: {}", body.substring(0, Math.min(body.length(), 100)) + "...");
        logger.info("--- END MOCK EMAIL ---");
        return true;
    }

    /**
     * Validate email format
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
