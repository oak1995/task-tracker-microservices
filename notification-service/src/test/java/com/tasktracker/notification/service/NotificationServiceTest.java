package com.tasktracker.notification.service;

import com.tasktracker.notification.dto.CreateNotificationRequest;
import com.tasktracker.notification.dto.NotificationDto;
import com.tasktracker.notification.model.Notification;
import com.tasktracker.notification.model.NotificationStatus;
import com.tasktracker.notification.model.NotificationType;
import com.tasktracker.notification.provider.NotificationProvider;
import com.tasktracker.notification.provider.NotificationProviderFactory;
import com.tasktracker.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class NotificationServiceTest {
    
    @Mock
    private NotificationRepository notificationRepository;
    
    @Mock
    private NotificationProviderFactory providerFactory;
    
    @Mock
    private UserNotificationSettingsService userNotificationSettingsService;
    
    @Mock
    private NotificationProvider notificationProvider;
    
    private NotificationService notificationService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationService = new NotificationService(
            notificationRepository,
            providerFactory,
            userNotificationSettingsService
        );
    }
    
    @Test
    void createNotification_Success() {
        // Given
        CreateNotificationRequest request = new CreateNotificationRequest(
            1L, "Test Title", "Test Content", NotificationType.TASK_CREATED, "EMAIL"
        );
        request.setRecipientEmail("test@example.com");
        
        Notification notification = new Notification(
            request.getUserId(),
            request.getTitle(),
            request.getContent(),
            request.getType(),
            request.getChannel()
        );
        notification.setId(UUID.randomUUID());
        notification.setRecipientEmail("test@example.com");
        
        when(userNotificationSettingsService.isNotificationEnabled(1L, NotificationType.TASK_CREATED, "EMAIL"))
            .thenReturn(true);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(providerFactory.getProvider("EMAIL")).thenReturn(Optional.of(notificationProvider));
        when(notificationProvider.isEnabled()).thenReturn(true);
        when(notificationProvider.sendNotification(any(Notification.class))).thenReturn(true);
        
        // When
        NotificationDto result = notificationService.createNotification(request);
        
        // Then
        assertNotNull(result);
        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getContent(), result.getContent());
        assertEquals(request.getType(), result.getType());
        assertEquals(request.getChannel(), result.getChannel());
        
        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(providerFactory).getProvider("EMAIL");
        verify(notificationProvider).sendNotification(any(Notification.class));
    }
    
    @Test
    void createNotification_NotificationDisabled() {
        // Given
        CreateNotificationRequest request = new CreateNotificationRequest(
            1L, "Test Title", "Test Content", NotificationType.TASK_CREATED, "EMAIL"
        );
        
        when(userNotificationSettingsService.isNotificationEnabled(1L, NotificationType.TASK_CREATED, "EMAIL"))
            .thenReturn(false);
        
        // When
        NotificationDto result = notificationService.createNotification(request);
        
        // Then
        assertNull(result);
        verify(notificationRepository, never()).save(any(Notification.class));
    }
    
    @Test
    void getUserNotifications_Success() {
        // Given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        
        Notification notification = new Notification(
            userId, "Test Title", "Test Content", NotificationType.TASK_CREATED, "EMAIL"
        );
        notification.setId(UUID.randomUUID());
        
        Page<Notification> notificationPage = new PageImpl<>(List.of(notification));
        when(notificationRepository.findByUserId(userId, pageable)).thenReturn(notificationPage);
        
        // When
        Page<NotificationDto> result = notificationService.getUserNotifications(userId, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Title", result.getContent().get(0).getTitle());
        
        verify(notificationRepository).findByUserId(userId, pageable);
    }
    
    @Test
    void getUnreadNotifications_Success() {
        // Given
        Long userId = 1L;
        
        Notification notification = new Notification(
            userId, "Test Title", "Test Content", NotificationType.TASK_CREATED, "EMAIL"
        );
        notification.setId(UUID.randomUUID());
        
        when(notificationRepository.findUnreadByUserId(userId)).thenReturn(List.of(notification));
        
        // When
        List<NotificationDto> result = notificationService.getUnreadNotifications(userId);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Title", result.get(0).getTitle());
        
        verify(notificationRepository).findUnreadByUserId(userId);
    }
    
    @Test
    void getUnreadNotificationsCount_Success() {
        // Given
        Long userId = 1L;
        Long expectedCount = 5L;
        
        when(notificationRepository.countUnreadByUserId(userId)).thenReturn(expectedCount);
        
        // When
        Long result = notificationService.getUnreadNotificationsCount(userId);
        
        // Then
        assertEquals(expectedCount, result);
        verify(notificationRepository).countUnreadByUserId(userId);
    }
    
    @Test
    void markAsRead_Success() {
        // Given
        UUID notificationId = UUID.randomUUID();
        Notification notification = new Notification(
            1L, "Test Title", "Test Content", NotificationType.TASK_CREATED, "EMAIL"
        );
        notification.setId(notificationId);
        
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        
        // When
        notificationService.markAsRead(notificationId);
        
        // Then
        verify(notificationRepository).findById(notificationId);
        verify(notificationRepository).save(any(Notification.class));
        assertNotNull(notification.getReadAt());
    }
    
    @Test
    void markAllAsRead_Success() {
        // Given
        Long userId = 1L;
        
        Notification notification1 = new Notification(
            userId, "Test Title 1", "Test Content 1", NotificationType.TASK_CREATED, "EMAIL"
        );
        notification1.setId(UUID.randomUUID());
        
        Notification notification2 = new Notification(
            userId, "Test Title 2", "Test Content 2", NotificationType.TASK_UPDATED, "EMAIL"
        );
        notification2.setId(UUID.randomUUID());
        
        when(notificationRepository.findUnreadByUserId(userId))
            .thenReturn(List.of(notification1, notification2));
        when(notificationRepository.saveAll(any(List.class)))
            .thenReturn(List.of(notification1, notification2));
        
        // When
        notificationService.markAllAsRead(userId);
        
        // Then
        verify(notificationRepository).findUnreadByUserId(userId);
        verify(notificationRepository).saveAll(any(List.class));
        assertNotNull(notification1.getReadAt());
        assertNotNull(notification2.getReadAt());
    }
    
    @Test
    void sendNotification_Success() {
        // Given
        Notification notification = new Notification(
            1L, "Test Title", "Test Content", NotificationType.TASK_CREATED, "EMAIL"
        );
        notification.setId(UUID.randomUUID());
        notification.setRecipientEmail("test@example.com");
        
        when(providerFactory.getProvider("EMAIL")).thenReturn(Optional.of(notificationProvider));
        when(notificationProvider.isEnabled()).thenReturn(true);
        when(notificationProvider.sendNotification(notification)).thenReturn(true);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        
        // When
        notificationService.sendNotification(notification);
        
        // Then
        assertEquals(NotificationStatus.SENT, notification.getStatus());
        assertNotNull(notification.getSentAt());
        
        verify(providerFactory).getProvider("EMAIL");
        verify(notificationProvider).sendNotification(notification);
        verify(notificationRepository).save(notification);
    }
    
    @Test
    void sendNotification_ProviderNotFound() {
        // Given
        Notification notification = new Notification(
            1L, "Test Title", "Test Content", NotificationType.TASK_CREATED, "UNKNOWN"
        );
        notification.setId(UUID.randomUUID());
        
        when(providerFactory.getProvider("UNKNOWN")).thenReturn(Optional.empty());
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        
        // When
        notificationService.sendNotification(notification);
        
        // Then
        assertEquals(NotificationStatus.FAILED, notification.getStatus());
        assertNotNull(notification.getErrorMessage());
        
        verify(providerFactory).getProvider("UNKNOWN");
        verify(notificationRepository).save(notification);
    }
    
    @Test
    void sendNotification_ProviderDisabled() {
        // Given
        Notification notification = new Notification(
            1L, "Test Title", "Test Content", NotificationType.TASK_CREATED, "EMAIL"
        );
        notification.setId(UUID.randomUUID());
        
        when(providerFactory.getProvider("EMAIL")).thenReturn(Optional.of(notificationProvider));
        when(notificationProvider.isEnabled()).thenReturn(false);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        
        // When
        notificationService.sendNotification(notification);
        
        // Then
        assertEquals(NotificationStatus.CANCELLED, notification.getStatus());
        assertNotNull(notification.getErrorMessage());
        
        verify(providerFactory).getProvider("EMAIL");
        verify(notificationProvider).isEnabled();
        verify(notificationRepository).save(notification);
    }
    
    @Test
    void sendNotification_ProviderFailed() {
        // Given
        Notification notification = new Notification(
            1L, "Test Title", "Test Content", NotificationType.TASK_CREATED, "EMAIL"
        );
        notification.setId(UUID.randomUUID());
        
        when(providerFactory.getProvider("EMAIL")).thenReturn(Optional.of(notificationProvider));
        when(notificationProvider.isEnabled()).thenReturn(true);
        when(notificationProvider.sendNotification(notification)).thenReturn(false);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        
        // When
        notificationService.sendNotification(notification);
        
        // Then
        assertEquals(NotificationStatus.FAILED, notification.getStatus());
        assertEquals(1, notification.getRetryCount());
        assertNotNull(notification.getErrorMessage());
        
        verify(providerFactory).getProvider("EMAIL");
        verify(notificationProvider).sendNotification(notification);
        verify(notificationRepository).save(notification);
    }
    
    @Test
    void retryFailedNotifications_Success() {
        // Given
        Notification failedNotification = new Notification(
            1L, "Test Title", "Test Content", NotificationType.TASK_CREATED, "EMAIL"
        );
        failedNotification.setId(UUID.randomUUID());
        failedNotification.setStatus(NotificationStatus.FAILED);
        failedNotification.setRetryCount(1);
        
        when(notificationRepository.findByStatusAndRetryCountLessThan(NotificationStatus.FAILED, 3))
            .thenReturn(List.of(failedNotification));
        when(providerFactory.getProvider("EMAIL")).thenReturn(Optional.of(notificationProvider));
        when(notificationProvider.isEnabled()).thenReturn(true);
        when(notificationProvider.sendNotification(failedNotification)).thenReturn(true);
        when(notificationRepository.save(any(Notification.class))).thenReturn(failedNotification);
        
        // When
        notificationService.retryFailedNotifications();
        
        // Then
        verify(notificationRepository).findByStatusAndRetryCountLessThan(NotificationStatus.FAILED, 3);
        verify(providerFactory).getProvider("EMAIL");
        verify(notificationProvider).sendNotification(failedNotification);
    }
    
    @Test
    void deleteNotification_Success() {
        // Given
        UUID notificationId = UUID.randomUUID();
        
        // When
        notificationService.deleteNotification(notificationId);
        
        // Then
        verify(notificationRepository).deleteById(notificationId);
    }
} 