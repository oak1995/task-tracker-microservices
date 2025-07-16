package com.tasktracker.notification.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasktracker.notification.dto.CreateNotificationRequest;
import com.tasktracker.notification.dto.UserNotificationSettingsDto;
import com.tasktracker.notification.model.NotificationType;
import com.tasktracker.notification.model.UserNotificationSettings;
import com.tasktracker.notification.repository.NotificationRepository;
import com.tasktracker.notification.repository.UserNotificationSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class NotificationServiceIntegrationTest {
    
    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserNotificationSettingsRepository settingsRepository;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
        
        // Clean up repositories
        notificationRepository.deleteAll();
        settingsRepository.deleteAll();
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createNotification_Success() throws Exception {
        // Given
        CreateNotificationRequest request = new CreateNotificationRequest(
            1L, "Test Title", "Test Content", NotificationType.TASK_CREATED, "EMAIL"
        );
        request.setRecipientEmail("test@example.com");
        
        // Create user settings first
        UserNotificationSettings settings = new UserNotificationSettings(1L, "test@example.com");
        settingsRepository.save(settings);
        
        // When & Then
        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Test Title"))
            .andExpect(jsonPath("$.content").value("Test Content"))
            .andExpect(jsonPath("$.type").value("TASK_CREATED"))
            .andExpect(jsonPath("$.channel").value("EMAIL"))
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.recipientEmail").value("test@example.com"));
    }
    
    @Test
    @WithMockUser(roles = "USER", username = "testuser")
    void getUserNotifications_Success() throws Exception {
        // Given
        Long userId = 1L;
        
        // Create user settings
        UserNotificationSettings settings = new UserNotificationSettings(userId, "test@example.com");
        settingsRepository.save(settings);
        
        // Create notification via API
        CreateNotificationRequest request = new CreateNotificationRequest(
            userId, "Test Title", "Test Content", NotificationType.TASK_CREATED, "EMAIL"
        );
        request.setRecipientEmail("test@example.com");
        
        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
        
        // When & Then
        mockMvc.perform(get("/api/notifications/user/{userId}", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].title").value("Test Title"))
            .andExpect(jsonPath("$.content[0].userId").value(userId.intValue()));
    }
    
    @Test
    @WithMockUser(roles = "USER", username = "testuser")
    void getUnreadNotifications_Success() throws Exception {
        // Given
        Long userId = 1L;
        
        // Create user settings
        UserNotificationSettings settings = new UserNotificationSettings(userId, "test@example.com");
        settingsRepository.save(settings);
        
        // Create notification via API
        CreateNotificationRequest request = new CreateNotificationRequest(
            userId, "Test Title", "Test Content", NotificationType.TASK_CREATED, "EMAIL"
        );
        request.setRecipientEmail("test@example.com");
        
        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
        
        // When & Then
        mockMvc.perform(get("/api/notifications/user/{userId}/unread", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].title").value("Test Title"))
            .andExpect(jsonPath("$[0].userId").value(userId.intValue()));
    }
    
    @Test
    @WithMockUser(roles = "USER", username = "testuser")
    void getUnreadNotificationsCount_Success() throws Exception {
        // Given
        Long userId = 1L;
        
        // Create user settings
        UserNotificationSettings settings = new UserNotificationSettings(userId, "test@example.com");
        settingsRepository.save(settings);
        
        // Create notification via API
        CreateNotificationRequest request = new CreateNotificationRequest(
            userId, "Test Title", "Test Content", NotificationType.TASK_CREATED, "EMAIL"
        );
        request.setRecipientEmail("test@example.com");
        
        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
        
        // When & Then
        mockMvc.perform(get("/api/notifications/user/{userId}/unread/count", userId))
            .andExpect(status().isOk())
            .andExpect(content().string("1"));
    }
    
    @Test
    @WithMockUser(roles = "USER", username = "testuser")
    void createUserNotificationSettings_Success() throws Exception {
        // Given
        Long userId = 1L;
        String email = "test@example.com";
        
        // When & Then
        mockMvc.perform(post("/api/notification-settings/user/{userId}", userId)
                .param("email", email))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").value(userId.intValue()))
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.emailNotifications").value(true))
            .andExpect(jsonPath("$.taskCreated").value(true))
            .andExpect(jsonPath("$.taskUpdated").value(true));
    }
    
    @Test
    @WithMockUser(roles = "USER", username = "testuser")
    void getUserNotificationSettings_Success() throws Exception {
        // Given
        Long userId = 1L;
        String email = "test@example.com";
        
        UserNotificationSettings settings = new UserNotificationSettings(userId, email);
        settingsRepository.save(settings);
        
        // When & Then
        mockMvc.perform(get("/api/notification-settings/user/{userId}", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(userId.intValue()))
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.emailNotifications").value(true));
    }
    
    @Test
    @WithMockUser(roles = "USER", username = "testuser")
    void updateUserNotificationSettings_Success() throws Exception {
        // Given
        Long userId = 1L;
        String email = "test@example.com";
        
        UserNotificationSettings settings = new UserNotificationSettings(userId, email);
        settingsRepository.save(settings);
        
        UserNotificationSettingsDto updateDto = new UserNotificationSettingsDto();
        updateDto.setEmailNotifications(false);
        updateDto.setTaskCreated(false);
        
        // When & Then
        mockMvc.perform(put("/api/notification-settings/user/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(userId.intValue()))
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.emailNotifications").value(false))
            .andExpect(jsonPath("$.taskCreated").value(false));
    }
    
    @Test
    @WithMockUser(roles = "USER", username = "testuser")
    void toggleEmailNotifications_Success() throws Exception {
        // Given
        Long userId = 1L;
        String email = "test@example.com";
        
        UserNotificationSettings settings = new UserNotificationSettings(userId, email);
        settingsRepository.save(settings);
        
        // When & Then
        mockMvc.perform(put("/api/notification-settings/user/{userId}/email-notifications", userId)
                .param("enabled", "false"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(userId.intValue()))
            .andExpect(jsonPath("$.emailNotifications").value(false));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void initializeDefaultTemplates_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/notification-templates/initialize-defaults"))
            .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void getActiveTemplates_Success() throws Exception {
        // Given - Initialize default templates first
        mockMvc.perform(post("/api/notification-templates/initialize-defaults"))
            .andExpect(status().isOk());
        
        // When & Then
        mockMvc.perform(get("/api/notification-templates"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }
    
    @Test
    void unauthorizedAccess_ReturnsUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/notifications/user/1"))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void accessOtherUserNotifications_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/notifications/user/999"))
            .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void adminOnlyEndpoint_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/notifications/retry-failed"))
            .andExpect(status().isForbidden());
    }
} 