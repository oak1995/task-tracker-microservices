package com.tasktracker.notification.controller;

import com.tasktracker.notification.model.NotificationTemplate;
import com.tasktracker.notification.model.NotificationType;
import com.tasktracker.notification.service.NotificationTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/notification-templates")
@Tag(name = "Notification Templates", description = "Notification template management API")
public class NotificationTemplateController {
    
    private final NotificationTemplateService templateService;
    
    public NotificationTemplateController(NotificationTemplateService templateService) {
        this.templateService = templateService;
    }
    
    @GetMapping
    @Operation(summary = "Get all active templates", description = "Gets all active notification templates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationTemplate>> getActiveTemplates() {
        List<NotificationTemplate> templates = templateService.getActiveTemplates();
        return ResponseEntity.ok(templates);
    }
    
    @GetMapping("/type/{type}")
    @Operation(summary = "Get templates by type", description = "Gets notification templates by notification type")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationTemplate>> getTemplatesByType(
            @Parameter(description = "Notification type") @PathVariable NotificationType type) {
        
        List<NotificationTemplate> templates = templateService.getTemplatesByType(type);
        return ResponseEntity.ok(templates);
    }
    
    @GetMapping("/channel/{channel}")
    @Operation(summary = "Get templates by channel", description = "Gets notification templates by channel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationTemplate>> getTemplatesByChannel(
            @Parameter(description = "Notification channel") @PathVariable String channel) {
        
        List<NotificationTemplate> templates = templateService.getTemplatesByChannel(channel);
        return ResponseEntity.ok(templates);
    }
    
    @GetMapping("/name/{name}")
    @Operation(summary = "Get template by name", description = "Gets a notification template by its name")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationTemplate> getTemplateByName(
            @Parameter(description = "Template name") @PathVariable String name) {
        
        Optional<NotificationTemplate> template = templateService.getTemplateByName(name);
        if (template.isPresent()) {
            return ResponseEntity.ok(template.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{type}/{channel}")
    @Operation(summary = "Get template by type and channel", description = "Gets a notification template by type and channel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationTemplate> getTemplate(
            @Parameter(description = "Notification type") @PathVariable NotificationType type,
            @Parameter(description = "Notification channel") @PathVariable String channel) {
        
        Optional<NotificationTemplate> template = templateService.getTemplate(type, channel);
        if (template.isPresent()) {
            return ResponseEntity.ok(template.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping
    @Operation(summary = "Create a new template", description = "Creates a new notification template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationTemplate> createTemplate(
            @Valid @RequestBody NotificationTemplate template) {
        
        try {
            NotificationTemplate createdTemplate = templateService.createTemplate(template);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTemplate);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{templateId}")
    @Operation(summary = "Update a template", description = "Updates an existing notification template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationTemplate> updateTemplate(
            @Parameter(description = "Template ID") @PathVariable UUID templateId,
            @Valid @RequestBody NotificationTemplate template) {
        
        try {
            NotificationTemplate updatedTemplate = templateService.updateTemplate(templateId, template);
            return ResponseEntity.ok(updatedTemplate);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{templateId}")
    @Operation(summary = "Delete a template", description = "Deletes a notification template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTemplate(
            @Parameter(description = "Template ID") @PathVariable UUID templateId) {
        
        templateService.deleteTemplate(templateId);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{templateId}/activate")
    @Operation(summary = "Activate a template", description = "Activates a notification template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateTemplate(
            @Parameter(description = "Template ID") @PathVariable UUID templateId) {
        
        templateService.activateTemplate(templateId);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{templateId}/deactivate")
    @Operation(summary = "Deactivate a template", description = "Deactivates a notification template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateTemplate(
            @Parameter(description = "Template ID") @PathVariable UUID templateId) {
        
        templateService.deactivateTemplate(templateId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/initialize-defaults")
    @Operation(summary = "Initialize default templates", description = "Initializes default notification templates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> initializeDefaultTemplates() {
        templateService.initializeDefaultTemplates();
        return ResponseEntity.ok().build();
    }
} 