package com.tasktracker.notification.controller;

import com.tasktracker.notification.dto.CreateNotificationRequest;
import com.tasktracker.notification.dto.NotificationDto;
import com.tasktracker.notification.model.NotificationStatus;
import com.tasktracker.notification.model.NotificationType;
import com.tasktracker.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Notification management API")
public class NotificationController {
    
    private final NotificationService notificationService;
    
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    @PostMapping
    @Operation(summary = "Create a new notification", description = "Creates a new notification and attempts to send it")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public ResponseEntity<NotificationDto> createNotification(
            @Valid @RequestBody CreateNotificationRequest request) {
        
        NotificationDto notification = notificationService.createNotification(request);
        if (notification == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(notification);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user notifications", description = "Gets paginated notifications for a specific user")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Page<NotificationDto>> getUserNotifications(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<NotificationDto> notifications = notificationService.getUserNotifications(userId, pageable);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/user/{userId}/status/{status}")
    @Operation(summary = "Get user notifications by status", description = "Gets user notifications filtered by status")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Page<NotificationDto>> getUserNotificationsByStatus(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Notification status") @PathVariable NotificationStatus status,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<NotificationDto> notifications = notificationService.getUserNotificationsByStatus(userId, status, pageable);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/user/{userId}/type/{type}")
    @Operation(summary = "Get user notifications by type", description = "Gets user notifications filtered by type")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Page<NotificationDto>> getUserNotificationsByType(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Notification type") @PathVariable NotificationType type,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<NotificationDto> notifications = notificationService.getUserNotificationsByType(userId, type, pageable);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/user/{userId}/unread")
    @Operation(summary = "Get unread notifications", description = "Gets all unread notifications for a user")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        
        List<NotificationDto> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/user/{userId}/unread/count")
    @Operation(summary = "Get unread notifications count", description = "Gets the count of unread notifications for a user")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Long> getUnreadNotificationsCount(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        
        Long count = notificationService.getUnreadNotificationsCount(userId);
        return ResponseEntity.ok(count);
    }
    
    @PutMapping("/{notificationId}/read")
    @Operation(summary = "Mark notification as read", description = "Marks a specific notification as read")
    @PreAuthorize("hasRole('ADMIN') or @notificationService.isOwner(#notificationId, authentication.principal.id)")
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "Notification ID") @PathVariable UUID notificationId) {
        
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/user/{userId}/read-all")
    @Operation(summary = "Mark all notifications as read", description = "Marks all notifications as read for a user")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Void> markAllAsRead(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Delete notification", description = "Deletes a specific notification")
    @PreAuthorize("hasRole('ADMIN') or @notificationService.isOwner(#notificationId, authentication.principal.id)")
    public ResponseEntity<Void> deleteNotification(
            @Parameter(description = "Notification ID") @PathVariable UUID notificationId) {
        
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/retry-failed")
    @Operation(summary = "Retry failed notifications", description = "Retries sending failed notifications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> retryFailedNotifications() {
        notificationService.retryFailedNotifications();
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/cleanup")
    @Operation(summary = "Cleanup old notifications", description = "Cleans up old notifications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cleanupOldNotifications() {
        notificationService.cleanupOldNotifications();
        return ResponseEntity.ok().build();
    }
} 