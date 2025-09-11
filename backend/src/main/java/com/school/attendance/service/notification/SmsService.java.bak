package com.school.attendance.service.notification;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class SmsService {
    
    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);
    
    @Value("${twilio.account.sid:}")
    private String accountSid;
    
    @Value("${twilio.auth.token:}")
    private String authToken;
    
    @Value("${twilio.phone.number:}")
    private String fromPhoneNumber;
    
    @Value("${notification.sms.enabled:false}")
    private boolean smsEnabled;
    
    private boolean twilioConfigured = false;

    @PostConstruct
    public void initializeTwilio() {
        if (accountSid != null && !accountSid.trim().isEmpty() && 
            authToken != null && !authToken.trim().isEmpty()) {
            try {
                Twilio.init(accountSid, authToken);
                twilioConfigured = true;
                logger.info("Twilio initialized successfully");
            } catch (Exception e) {
                logger.error("Failed to initialize Twilio: {}", e.getMessage());
                twilioConfigured = false;
            }
        } else {
            logger.info("Twilio credentials not configured, using mock SMS service");
            twilioConfigured = false;
        }
    }

    /**
     * Send SMS using Twilio or mock service
     */
    public boolean sendSms(String phoneNumber, String message) {
        try {
            // If SMS is disabled or Twilio is not configured, use mock
            if (!smsEnabled || !twilioConfigured) {
                return sendMockSms(phoneNumber, message);
            }
            
            // Validate phone number format
            if (!isValidPhoneNumber(phoneNumber)) {
                logger.error("Invalid phone number format: {}", phoneNumber);
                return false;
            }
            
            Message twilioMessage = Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(fromPhoneNumber),
                message
            ).create();
            
            logger.info("SMS sent successfully to: {} with SID: {}", phoneNumber, twilioMessage.getSid());
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage());
            return false;
        }
    }

    /**
     * Mock SMS sending for development/testing
     */
    private boolean sendMockSms(String phoneNumber, String message) {
        logger.info("MOCK SMS SENT:");
        logger.info("To: {}", phoneNumber);
        logger.info("Message: {}", message);
        logger.info("--- END MOCK SMS ---");
        return true;
    }

    /**
     * Validate phone number format
     * Supports formats: +1234567890, +91-9876543210, etc.
     */
    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        
        // Remove spaces and dashes
        String cleaned = phoneNumber.replaceAll("[\\s-]", "");
        
        // Check if it starts with + and has 10-15 digits
        return cleaned.matches("^\\+[1-9]\\d{9,14}$");
    }

    /**
     * Format phone number to international format
     */
    public String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return phoneNumber;
        }
        
        // Remove all non-digit characters except +
        String cleaned = phoneNumber.replaceAll("[^+\\d]", "");
        
        // If it doesn't start with +, assume it's an Indian number
        if (!cleaned.startsWith("+")) {
            // Remove leading 0 if present
            if (cleaned.startsWith("0")) {
                cleaned = cleaned.substring(1);
            }
            // Add +91 for India
            cleaned = "+91" + cleaned;
        }
        
        return cleaned;
    }

    /**
     * Check if Twilio is properly configured
     */
    public boolean isTwilioConfigured() {
        return twilioConfigured;
    }
}
