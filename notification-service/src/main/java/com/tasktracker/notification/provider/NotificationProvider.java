package com.tasktracker.notification.provider;

import com.tasktracker.notification.model.Notification;

public interface NotificationProvider {
    
    /**
     * Send notification using this provider
     * @param notification The notification to send
     * @return true if sent successfully, false otherwise
     */
    boolean sendNotification(Notification notification);
    
    /**
     * Get the channel this provider handles
     * @return The channel name (EMAIL, PUSH, SMS, etc.)
     */
    String getChannel();
    
    /**
     * Check if this provider is enabled
     * @return true if enabled, false otherwise
     */
    boolean isEnabled();
} 