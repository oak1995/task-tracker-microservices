package com.tasktracker.notification.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasktracker.notification.dto.AuthEventDto;
import com.tasktracker.notification.dto.CreateNotificationRequest;
import com.tasktracker.notification.model.NotificationType;
import com.tasktracker.notification.service.NotificationService;
import com.tasktracker.notification.service.UserNotificationSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AuthEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthEventListener.class);
    
    private final NotificationService notificationService;
    private final UserNotificationSettingsService userNotificationSettingsService;
    private final ObjectMapper objectMapper;
    
    public AuthEventListener(NotificationService notificationService, 
                           UserNotificationSettingsService userNotificationSettingsService,
                           ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.userNotificationSettingsService = userNotificationSettingsService;
        this.objectMapper = objectMapper;
    }
    
    @KafkaListener(topics = "auth-events", groupId = "notification-service-group")
    public void handleAuthEvent(String message) {
        logger.info("Received auth event: {}", message);
        
        try {
            AuthEventDto authEvent = objectMapper.readValue(message, AuthEventDto.class);
            processAuthEvent(authEvent);
        } catch (Exception e) {
            logger.error("Error processing auth event: {}", message, e);
        }
    }
    
    private void processAuthEvent(AuthEventDto authEvent) {
        logger.debug("Processing auth event: {}", authEvent.getEventType());
        
        switch (authEvent.getEventType()) {
            case "USER_REGISTERED":
                handleUserRegistered(authEvent);
                break;
            case "USER_LOGIN":
                handleUserLogin(authEvent);
                break;
            case "PASSWORD_RESET":
                handlePasswordReset(authEvent);
                break;
            default:
                logger.warn("Unknown auth event type: {}", authEvent.getEventType());
        }
    }
    
    private void handleUserRegistered(AuthEventDto authEvent) {
        logger.info("Processing user registration for userId: {}", authEvent.getUserId());
        
        // Create default notification settings for the new user
        try {
            userNotificationSettingsService.createDefaultSettings(authEvent.getUserId(), authEvent.getEmail());
        } catch (Exception e) {
            logger.error("Error creating default notification settings for user {}", authEvent.getUserId(), e);
        }
        
        // Send welcome notification
        CreateNotificationRequest request = new CreateNotificationRequest(
            authEvent.getUserId(),
            "Welcome to Task Tracker!",
            "Welcome " + authEvent.getUsername() + "! Your account has been created successfully. " +
                "You can now start managing your tasks and receive notifications about important updates.",
            NotificationType.USER_REGISTERED,
            "EMAIL"
        );
        request.setRecipientEmail(authEvent.getEmail());
        request.setMetadata(createMetadata(authEvent));
        
        notificationService.createNotification(request);
    }
    
    private void handleUserLogin(AuthEventDto authEvent) {
        logger.debug("Processing user login for userId: {}", authEvent.getUserId());
        
        // Only send login notification if it's from a new IP or suspicious activity
        if (isSuspiciousLogin(authEvent)) {
            CreateNotificationRequest request = new CreateNotificationRequest(
                authEvent.getUserId(),
                "New Login Detected",
                "A new login to your Task Tracker account was detected from IP: " + 
                    authEvent.getIpAddress() + " at " + authEvent.getEventTime(),
                NotificationType.USER_LOGIN,
                "EMAIL"
            );
            request.setRecipientEmail(authEvent.getEmail());
            request.setMetadata(createMetadata(authEvent));
            
            notificationService.createNotification(request);
        }
    }
    
    private void handlePasswordReset(AuthEventDto authEvent) {
        logger.info("Processing password reset for userId: {}", authEvent.getUserId());
        
        CreateNotificationRequest request = new CreateNotificationRequest(
            authEvent.getUserId(),
            "Password Reset Request",
            "A password reset request has been made for your account. " +
                "If you didn't request this, please contact support immediately.",
            NotificationType.SYSTEM_ALERT,
            "EMAIL"
        );
        request.setRecipientEmail(authEvent.getEmail());
        request.setMetadata(createMetadata(authEvent));
        
        notificationService.createNotification(request);
    }
    
    private boolean isSuspiciousLogin(AuthEventDto authEvent) {
        // Simple logic - in a real app, this would check against known IPs, locations, etc.
        // For now, we'll just check if there's an IP address (indicating it's tracked)
        return authEvent.getIpAddress() != null && !authEvent.getIpAddress().isEmpty();
    }
    
    private String createMetadata(AuthEventDto authEvent) {
        try {
            return objectMapper.writeValueAsString(authEvent);
        } catch (Exception e) {
            logger.error("Error creating metadata", e);
            return "{}";
        }
    }
} 