package com.linkedIn.linkedIn.features.notifications.controller;

import com.linkedIn.linkedIn.features.authentication.model.AuthenticationUser;
import com.linkedIn.linkedIn.features.notifications.model.Notification;
import com.linkedIn.linkedIn.features.notifications.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        List<Notification> notifications = notificationService.getAllNotifications(user.getId());
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{notificationId}")
    public ResponseEntity<Void> markNotificationRead(@RequestAttribute("authenticatedUser") AuthenticationUser user, @PathVariable Long notificationId) {
        notificationService.markNotificationRead(user.getId(), notificationId);
        return ResponseEntity.noContent().build();
    }
}
