package com.tasktracker.notification.service;

import com.tasktracker.notification.dto.CreateNotificationRequest;
import com.tasktracker.notification.dto.NotificationDto;
import com.tasktracker.notification.model.Notification;
import com.tasktracker.notification.model.NotificationStatus;
import com.tasktracker.notification.model.NotificationType;
import com.tasktracker.notification.provider.NotificationProvider;
import com.tasktracker.notification.provider.NotificationProviderFactory;
import com.tasktracker.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    private final NotificationRepository notificationRepository;
    private final NotificationProviderFactory providerFactory;
    private final UserNotificationSettingsService userNotificationSettingsService;
    
    public NotificationService(NotificationRepository notificationRepository,
                             NotificationProviderFactory providerFactory,
                             UserNotificationSettingsService userNotificationSettingsService) {
        this.notificationRepository = notificationRepository;
        this.providerFactory = providerFactory;
        this.userNotificationSettingsService = userNotificationSettingsService;
    }
    
    public NotificationDto createNotification(CreateNotificationRequest request) {
        logger.info("Creating notification for user: {}", request.getUserId());
        
        // Check if user has enabled this type of notification
        if (!userNotificationSettingsService.isNotificationEnabled(request.getUserId(), request.getType(), request.getChannel())) {
            logger.info("Notification disabled for user: {} type: {} channel: {}", 
                       request.getUserId(), request.getType(), request.getChannel());
            return null;
        }
        
        // Get user email if not provided
        String recipientEmail = request.getRecipientEmail();
        if (recipientEmail == null && "EMAIL".equals(request.getChannel())) {
            recipientEmail = userNotificationSettingsService.getUserEmail(request.getUserId());
        }
        
        Notification notification = new Notification(
            request.getUserId(),
            request.getTitle(),
            request.getContent(),
            request.getType(),
            request.getChannel()
        );
        
        notification.setRecipientEmail(recipientEmail);
        notification.setMetadata(request.getMetadata());
        
        notification = notificationRepository.save(notification);
        
        // Try to send notification immediately
        sendNotificationAsync(notification);
        
        return convertToDto(notification);
    }
    
    private void sendNotificationAsync(Notification notification) {
        // In a real application, this would be done asynchronously
        // For now, we'll call it synchronously
        sendNotification(notification);
    }
    
    @Transactional
    public void sendNotification(Notification notification) {
        logger.info("Sending notification: {}", notification.getId());
        
        Optional<NotificationProvider> provider = providerFactory.getProvider(notification.getChannel());
        if (provider.isEmpty()) {
            logger.error("No provider found for channel: {}", notification.getChannel());
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage("No provider found for channel: " + notification.getChannel());
            notificationRepository.save(notification);
            return;
        }
        
        if (!provider.get().isEnabled()) {
            logger.warn("Provider disabled for channel: {}", notification.getChannel());
            notification.setStatus(NotificationStatus.CANCELLED);
            notification.setErrorMessage("Provider disabled for channel: " + notification.getChannel());
            notificationRepository.save(notification);
            return;
        }
        
        try {
            boolean sent = provider.get().sendNotification(notification);
            if (sent) {
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
                logger.info("Notification sent successfully: {}", notification.getId());
            } else {
                notification.setStatus(NotificationStatus.FAILED);
                notification.setErrorMessage("Provider failed to send notification");
                notification.setRetryCount(notification.getRetryCount() + 1);
                logger.error("Failed to send notification: {}", notification.getId());
            }
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            notification.setRetryCount(notification.getRetryCount() + 1);
            logger.error("Error sending notification: {}", notification.getId(), e);
        }
        
        notificationRepository.save(notification);
    }
    
    @Transactional(readOnly = true)
    public Page<NotificationDto> getUserNotifications(Long userId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByUserId(userId, pageable);
        return notifications.map(this::convertToDto);
    }
    
    @Transactional(readOnly = true)
    public Page<NotificationDto> getUserNotificationsByStatus(Long userId, NotificationStatus status, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByUserIdAndStatus(userId, status, pageable);
        return notifications.map(this::convertToDto);
    }
    
    @Transactional(readOnly = true)
    public Page<NotificationDto> getUserNotificationsByType(Long userId, NotificationType type, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByUserIdAndType(userId, type, pageable);
        return notifications.map(this::convertToDto);
    }
    
    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findUnreadByUserId(userId);
        return notifications.stream().map(this::convertToDto).toList();
    }
    
    @Transactional(readOnly = true)
    public Long getUnreadNotificationsCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }
    
    @Transactional
    public void markAsRead(UUID notificationId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (notification.isPresent()) {
            notification.get().setReadAt(LocalDateTime.now());
            notificationRepository.save(notification.get());
            logger.info("Notification marked as read: {}", notificationId);
        }
    }
    
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findUnreadByUserId(userId);
        LocalDateTime now = LocalDateTime.now();
        
        unreadNotifications.forEach(notification -> notification.setReadAt(now));
        notificationRepository.saveAll(unreadNotifications);
        
        logger.info("All notifications marked as read for user: {}", userId);
    }
    
    @Transactional
    public void deleteNotification(UUID notificationId) {
        notificationRepository.deleteById(notificationId);
        logger.info("Notification deleted: {}", notificationId);
    }
    
    @Transactional
    public void retryFailedNotifications() {
        List<Notification> failedNotifications = notificationRepository.findByStatusAndRetryCountLessThan(
            NotificationStatus.FAILED, 3);
        
        logger.info("Retrying {} failed notifications", failedNotifications.size());
        
        failedNotifications.forEach(this::sendNotification);
    }
    
    @Transactional
    public void cleanupOldNotifications() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        List<Notification> oldNotifications = notificationRepository.findOldNotifications(
            NotificationStatus.SENT, threshold);
        
        logger.info("Cleaning up {} old notifications", oldNotifications.size());
        
        notificationRepository.deleteAll(oldNotifications);
    }
    
    private NotificationDto convertToDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUserId());
        dto.setTitle(notification.getTitle());
        dto.setContent(notification.getContent());
        dto.setType(notification.getType());
        dto.setStatus(notification.getStatus());
        dto.setChannel(notification.getChannel());
        dto.setRecipientEmail(notification.getRecipientEmail());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setSentAt(notification.getSentAt());
        dto.setReadAt(notification.getReadAt());
        dto.setErrorMessage(notification.getErrorMessage());
        dto.setRetryCount(notification.getRetryCount());
        dto.setMetadata(notification.getMetadata());
        return dto;
    }
} 