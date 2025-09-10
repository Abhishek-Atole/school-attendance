package com.school.attendance.exception;

/**
 * Exception for notification service errors
 */
public class NotificationException extends RuntimeException {
    
    private final String notificationType;
    private final String recipient;
    
    public NotificationException(String message) {
        super(message);
        this.notificationType = null;
        this.recipient = null;
    }
    
    public NotificationException(String message, Throwable cause) {
        super(message, cause);
        this.notificationType = null;
        this.recipient = null;
    }
    
    public NotificationException(String message, String notificationType, String recipient) {
        super(message);
        this.notificationType = notificationType;
        this.recipient = recipient;
    }
    
    public NotificationException(String message, Throwable cause, String notificationType, String recipient) {
        super(message, cause);
        this.notificationType = notificationType;
        this.recipient = recipient;
    }
    
    public String getNotificationType() {
        return notificationType;
    }
    
    public String getRecipient() {
        return recipient;
    }
}
