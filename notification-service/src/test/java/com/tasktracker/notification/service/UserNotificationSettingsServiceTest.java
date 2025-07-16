package com.tasktracker.notification.service;

import com.tasktracker.notification.dto.UserNotificationSettingsDto;
import com.tasktracker.notification.model.NotificationType;
import com.tasktracker.notification.model.UserNotificationSettings;
import com.tasktracker.notification.repository.UserNotificationSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserNotificationSettingsServiceTest {
    
    @Mock
    private UserNotificationSettingsRepository settingsRepository;
    
    private UserNotificationSettingsService settingsService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        settingsService = new UserNotificationSettingsService(settingsRepository);
    }
    
    @Test
    void createDefaultSettings_Success() {
        // Given
        Long userId = 1L;
        String email = "test@example.com";
        
        UserNotificationSettings settings = new UserNotificationSettings(userId, email);
        settings.setId(java.util.UUID.randomUUID());
        
        when(settingsRepository.existsByUserId(userId)).thenReturn(false);
        when(settingsRepository.save(any(UserNotificationSettings.class))).thenReturn(settings);
        
        // When
        UserNotificationSettingsDto result = settingsService.createDefaultSettings(userId, email);
        
        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(email, result.getEmail());
        assertTrue(result.getEmailNotifications());
        assertTrue(result.getTaskCreated());
        
        verify(settingsRepository).existsByUserId(userId);
        verify(settingsRepository).save(any(UserNotificationSettings.class));
    }
    
    @Test
    void createDefaultSettings_AlreadyExists() {
        // Given
        Long userId = 1L;
        String email = "test@example.com";
        
        UserNotificationSettings existingSettings = new UserNotificationSettings(userId, email);
        existingSettings.setId(java.util.UUID.randomUUID());
        
        when(settingsRepository.existsByUserId(userId)).thenReturn(true);
        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.of(existingSettings));
        
        // When
        UserNotificationSettingsDto result = settingsService.createDefaultSettings(userId, email);
        
        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(email, result.getEmail());
        
        verify(settingsRepository).existsByUserId(userId);
        verify(settingsRepository, never()).save(any(UserNotificationSettings.class));
    }
    
    @Test
    void getUserSettings_Success() {
        // Given
        Long userId = 1L;
        String email = "test@example.com";
        
        UserNotificationSettings settings = new UserNotificationSettings(userId, email);
        settings.setId(java.util.UUID.randomUUID());
        
        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.of(settings));
        
        // When
        UserNotificationSettingsDto result = settingsService.getUserSettings(userId);
        
        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(email, result.getEmail());
        
        verify(settingsRepository).findByUserId(userId);
    }
    
    @Test
    void getUserSettings_NotFound() {
        // Given
        Long userId = 1L;
        
        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.empty());
        
        // When
        UserNotificationSettingsDto result = settingsService.getUserSettings(userId);
        
        // Then
        assertNull(result);
        
        verify(settingsRepository).findByUserId(userId);
    }
    
    @Test
    void updateUserSettings_Success() {
        // Given
        Long userId = 1L;
        String email = "test@example.com";
        
        UserNotificationSettings existingSettings = new UserNotificationSettings(userId, email);
        existingSettings.setId(java.util.UUID.randomUUID());
        
        UserNotificationSettingsDto updateDto = new UserNotificationSettingsDto();
        updateDto.setEmailNotifications(false);
        updateDto.setTaskCreated(false);
        
        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.of(existingSettings));
        when(settingsRepository.save(any(UserNotificationSettings.class))).thenReturn(existingSettings);
        
        // When
        UserNotificationSettingsDto result = settingsService.updateUserSettings(userId, updateDto);
        
        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(email, result.getEmail());
        assertFalse(result.getEmailNotifications());
        assertFalse(result.getTaskCreated());
        
        verify(settingsRepository).findByUserId(userId);
        verify(settingsRepository).save(any(UserNotificationSettings.class));
    }
    
    @Test
    void updateUserSettings_NotFound() {
        // Given
        Long userId = 1L;
        UserNotificationSettingsDto updateDto = new UserNotificationSettingsDto();
        
        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            settingsService.updateUserSettings(userId, updateDto);
        });
        
        verify(settingsRepository).findByUserId(userId);
        verify(settingsRepository, never()).save(any(UserNotificationSettings.class));
    }
    
    @Test
    void isNotificationEnabled_EmailChannel_Enabled() {
        // Given
        Long userId = 1L;
        String email = "test@example.com";
        
        UserNotificationSettings settings = new UserNotificationSettings(userId, email);
        settings.setId(java.util.UUID.randomUUID());
        settings.setEmailNotifications(true);
        settings.setTaskCreated(true);
        
        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.of(settings));
        
        // When
        boolean result = settingsService.isNotificationEnabled(userId, NotificationType.TASK_CREATED, "EMAIL");
        
        // Then
        assertTrue(result);
        
        verify(settingsRepository).findByUserId(userId);
    }
    
    @Test
    void isNotificationEnabled_EmailChannel_Disabled() {
        // Given
        Long userId = 1L;
        String email = "test@example.com";
        
        UserNotificationSettings settings = new UserNotificationSettings(userId, email);
        settings.setId(java.util.UUID.randomUUID());
        settings.setEmailNotifications(false);
        settings.setTaskCreated(true);
        
        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.of(settings));
        
        // When
        boolean result = settingsService.isNotificationEnabled(userId, NotificationType.TASK_CREATED, "EMAIL");
        
        // Then
        assertFalse(result);
        
        verify(settingsRepository).findByUserId(userId);
    }
    
    @Test
    void isNotificationEnabled_TaskType_Disabled() {
        // Given
        Long userId = 1L;
        String email = "test@example.com";
        
        UserNotificationSettings settings = new UserNotificationSettings(userId, email);
        settings.setId(java.util.UUID.randomUUID());
        settings.setEmailNotifications(true);
        settings.setTaskCreated(false);
        
        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.of(settings));
        
        // When
        boolean result = settingsService.isNotificationEnabled(userId, NotificationType.TASK_CREATED, "EMAIL");
        
        // Then
        assertFalse(result);
        
        verify(settingsRepository).findByUserId(userId);
    }
    
    @Test
    void isNotificationEnabled_NoSettings_DefaultToEnabled() {
        // Given
        Long userId = 1L;
        
        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.empty());
        
        // When
        boolean result = settingsService.isNotificationEnabled(userId, NotificationType.TASK_CREATED, "EMAIL");
        
        // Then
        assertTrue(result);
        
        verify(settingsRepository).findByUserId(userId);
    }
    
    @Test
    void getUserEmail_Success() {
        // Given
        Long userId = 1L;
        String email = "test@example.com";
        
        UserNotificationSettings settings = new UserNotificationSettings(userId, email);
        settings.setId(java.util.UUID.randomUUID());
        
        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.of(settings));
        
        // When
        String result = settingsService.getUserEmail(userId);
        
        // Then
        assertEquals(email, result);
        
        verify(settingsRepository).findByUserId(userId);
    }
    
    @Test
    void getUserEmail_NotFound() {
        // Given
        Long userId = 1L;
        
        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.empty());
        
        // When
        String result = settingsService.getUserEmail(userId);
        
        // Then
        assertNull(result);
        
        verify(settingsRepository).findByUserId(userId);
    }
    
    @Test
    void deleteUserSettings_Success() {
        // Given
        Long userId = 1L;
        
        // When
        settingsService.deleteUserSettings(userId);
        
        // Then
        verify(settingsRepository).deleteByUserId(userId);
    }
} 