package com.school.attendance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Controller for handling internationalization (i18n) requests
 * Provides translated messages for the frontend application
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"}, allowCredentials = "true")
public class I18nController {

    @Autowired
    private MessageSource messageSource;

    /**
     * Get all translated messages for a specific language
     * 
     * @param lang Language code (en, hi, mr)
     * @return Map of all translated messages
     */
    @GetMapping("/messages")
    public ResponseEntity<Map<String, String>> getMessages(
            @RequestParam(defaultValue = "en") String lang) {
        
        try {
            Locale locale = Locale.forLanguageTag(lang);
            Map<String, String> messages = getAllMessages(locale);
            
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            // Fallback to English if there's an error
            Map<String, String> fallbackMessages = getAllMessages(Locale.ENGLISH);
            return ResponseEntity.ok(fallbackMessages);
        }
    }

    /**
     * Get a specific translated message
     * 
     * @param key Message key
     * @param lang Language code
     * @return Translated message
     */
    @GetMapping("/messages/{key}")
    public ResponseEntity<Map<String, String>> getMessage(
            @PathVariable String key,
            @RequestParam(defaultValue = "en") String lang) {
        
        try {
            Locale locale = Locale.forLanguageTag(lang);
            String message = messageSource.getMessage(key, null, key, locale);
            
            Map<String, String> response = new HashMap<>();
            response.put("key", key);
            response.put("message", message);
            response.put("language", lang);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("key", key);
            errorResponse.put("message", key); // Fallback to key itself
            errorResponse.put("language", lang);
            errorResponse.put("error", "Message not found");
            
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * Get information about supported languages
     * 
     * @return List of supported languages with their details
     */
    @GetMapping("/languages")
    public ResponseEntity<Map<String, Object>> getSupportedLanguages() {
        Map<String, Object> languages = new HashMap<>();
        
        Map<String, Object> english = new HashMap<>();
        english.put("code", "en");
        english.put("name", "English");
        english.put("nativeName", "English");
        english.put("flag", "🇺🇸");
        english.put("direction", "ltr");
        
        Map<String, Object> hindi = new HashMap<>();
        hindi.put("code", "hi");
        hindi.put("name", "Hindi");
        hindi.put("nativeName", "हिंदी");
        hindi.put("flag", "🇮🇳");
        hindi.put("direction", "ltr");
        
        Map<String, Object> marathi = new HashMap<>();
        marathi.put("code", "mr");
        marathi.put("name", "Marathi");
        marathi.put("nativeName", "मराठी");
        marathi.put("flag", "🇮🇳");
        marathi.put("direction", "ltr");
        
        languages.put("en", english);
        languages.put("hi", hindi);
        languages.put("mr", marathi);
        
        Map<String, Object> response = new HashMap<>();
        response.put("languages", languages);
        response.put("default", "en");
        response.put("count", 3);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to get all messages for a locale
     * 
     * @param locale Target locale
     * @return Map of all messages
     */
    private Map<String, String> getAllMessages(Locale locale) {
        Map<String, String> messages = new HashMap<>();
        
        // Define all message keys that should be available
        String[] messageKeys = {
            // App info
            "app.name", "app.version", "app.description",
            
            // Navigation
            "nav.dashboard", "nav.students", "nav.teachers", "nav.attendance", 
            "nav.reports", "nav.settings", "nav.logout",
            
            // Authentication
            "auth.login", "auth.logout", "auth.username", "auth.password",
            "auth.login.success", "auth.login.error", "auth.logout.success",
            
            // Dashboard
            "dashboard.title", "dashboard.total.students", "dashboard.total.teachers",
            "dashboard.present.today", "dashboard.absent.today", "dashboard.attendance.rate",
            "dashboard.recent.activities",
            
            // Students
            "student.title", "student.add", "student.edit", "student.delete", "student.view",
            "student.first.name", "student.last.name", "student.roll.number", "student.class",
            "student.section", "student.gender", "student.date.of.birth", "student.parent.name",
            "student.parent.mobile", "student.parent.email", "student.address",
            "student.added.success", "student.updated.success", "student.deleted.success",
            "student.not.found",
            
            // Teachers
            "teacher.title", "teacher.add", "teacher.edit", "teacher.delete", "teacher.view",
            "teacher.first.name", "teacher.last.name", "teacher.employee.number",
            "teacher.subject", "teacher.classes", "teacher.email", "teacher.mobile",
            "teacher.added.success", "teacher.updated.success", "teacher.deleted.success",
            "teacher.not.found",
            
            // Attendance
            "attendance.title", "attendance.mark", "attendance.view", "attendance.date",
            "attendance.status", "attendance.present", "attendance.absent", "attendance.late",
            "attendance.half.day", "attendance.holiday", "attendance.sick.leave",
            "attendance.marked.success", "attendance.updated.success", "attendance.not.found",
            
            // Reports
            "report.title", "report.generate", "report.daily", "report.monthly",
            "report.student.wise", "report.class.wise", "report.date.range",
            "report.from.date", "report.to.date", "report.generated.success", "report.no.data",
            
            // Notifications
            "notification.title", "notification.send", "notification.sms", "notification.email",
            "notification.message", "notification.sent.success", "notification.low.attendance",
            "notification.absent.today", "notification.late.arrival",
            
            // Common actions
            "action.add", "action.edit", "action.delete", "action.view", "action.save",
            "action.cancel", "action.confirm", "action.back", "action.next",
            "action.previous", "action.search", "action.filter", "action.export", "action.import",
            
            // Validation
            "validation.required", "validation.email.invalid", "validation.mobile.invalid",
            "validation.date.invalid", "validation.number.invalid",
            
            // Messages
            "success.saved", "success.updated", "success.deleted", "success.operation.completed",
            "error.general", "error.network", "error.server", "error.unauthorized",
            "error.not.found", "error.validation",
            
            // Confirmations
            "confirm.delete", "confirm.logout", "confirm.action",
            
            // Status
            "status.active", "status.inactive", "status.pending", "status.completed", "status.cancelled",
            
            // Accessibility
            "accessibility.skip.to.content", "accessibility.main.navigation", "accessibility.search.form",
            "accessibility.language.selector", "accessibility.theme.toggle", "accessibility.close.dialog",
            "accessibility.open.menu", "accessibility.sort.ascending", "accessibility.sort.descending",
            
            // Additional
            "language.select", "profile.view"
        };
        
        // Get translated message for each key
        for (String key : messageKeys) {
            try {
                String message = messageSource.getMessage(key, null, key, locale);
                messages.put(key, message);
            } catch (Exception e) {
                // If message not found, use the key itself as fallback
                messages.put(key, key);
            }
        }
        
        return messages;
    }
}
