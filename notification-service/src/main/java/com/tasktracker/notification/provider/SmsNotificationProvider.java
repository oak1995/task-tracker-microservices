package com.tasktracker.notification.provider;

import com.tasktracker.notification.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsNotificationProvider implements NotificationProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(SmsNotificationProvider.class);
    
    @Value("${notification.sms.enabled:false}")
    private boolean enabled;
    
    @Override
    public boolean sendNotification(Notification notification) {
        if (!enabled) {
            logger.warn("SMS notifications are disabled");
            return false;
        }
        
        try {
            // In a real implementation, this would integrate with:
            // - Twilio
            // - AWS SNS
            // - Nexmo/Vonage
            // - Other SMS providers
            
            logger.info("SMS notification would be sent to user: {} with title: {}", 
                       notification.getUserId(), notification.getTitle());
            
            // Simulate sending SMS
            Thread.sleep(150); // Simulate network delay
            
            // For now, we'll just log it as successful
            logger.info("SMS notification sent successfully to user: {}", notification.getUserId());
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to send SMS notification to user: {}", notification.getUserId(), e);
            return false;
        }
    }
    
    @Override
    public String getChannel() {
        return "SMS";
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
} 