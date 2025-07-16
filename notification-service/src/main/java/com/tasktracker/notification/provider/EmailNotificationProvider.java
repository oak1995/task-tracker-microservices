package com.tasktracker.notification.provider;

import com.tasktracker.notification.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationProvider implements NotificationProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationProvider.class);
    
    private final JavaMailSender mailSender;
    
    @Value("${notification.email.enabled:true}")
    private boolean enabled;
    
    @Value("${notification.email.from:noreply@tasktracker.com}")
    private String fromEmail;
    
    public EmailNotificationProvider(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    @Override
    public boolean sendNotification(Notification notification) {
        if (!enabled) {
            logger.warn("Email notifications are disabled");
            return false;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(notification.getRecipientEmail());
            message.setSubject(notification.getTitle());
            message.setText(notification.getContent());
            
            mailSender.send(message);
            
            logger.info("Email notification sent successfully to: {}", notification.getRecipientEmail());
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to send email notification to: {}", notification.getRecipientEmail(), e);
            return false;
        }
    }
    
    @Override
    public String getChannel() {
        return "EMAIL";
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
} 