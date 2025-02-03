package com.linkedIn.linkedIn.features.notifications.services;

import com.linkedIn.linkedIn.features.authentication.model.AuthenticationUser;
import com.linkedIn.linkedIn.features.authentication.repository.AuthenticationUserRepository;
import com.linkedIn.linkedIn.features.feed.model.Comment;
import com.linkedIn.linkedIn.features.notifications.dto.NotificationType;
import com.linkedIn.linkedIn.features.notifications.model.Notification;
import com.linkedIn.linkedIn.features.notifications.repository.NotificationRepository;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class NotificationService {

    private final MessageSendingOperations<String> messagingTemplate;
    private final AuthenticationUserRepository authenticationUserRepository;
    private final NotificationRepository notificationRepository;

    public NotificationService(MessageSendingOperations<String> messagingTemplate, AuthenticationUserRepository authenticationUserRepository, NotificationRepository notificationRepository) {
        this.messagingTemplate = messagingTemplate;
        this.authenticationUserRepository = authenticationUserRepository;
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getAllNotifications(Long userId) {
        AuthenticationUser user = authenticationUserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return notificationRepository.findByRecipient(user);
    }

    public void markNotificationRead(Long userId, Long notificationId) {
        AuthenticationUser user = authenticationUserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        if (!notification.recipient.equals(user)) {
            throw new IllegalArgumentException("User is not recipient of notification");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void sendLikeNotification(AuthenticationUser author, AuthenticationUser recipient, Long resourceId) {
        if (author.getId().equals(recipient.getId())) {
            return;
        }

        Notification notification = notificationRepository.save(new Notification(author, recipient, NotificationType.LIKE, resourceId));
        messagingTemplate.convertAndSend("/notifications/user/" + recipient.getId() + "/notification", notification);
    }

    public void sendLikeToPost(Long postId, Set<AuthenticationUser> likes) {
        messagingTemplate.convertAndSend("/notifications/post/" + postId + "/likes", likes);
    }

    public void sendCommentNotification(AuthenticationUser author, AuthenticationUser recipient, Long resourceId) {
        if (author.getId().equals(recipient.getId())) {
            return;
        }
        Notification notification = notificationRepository.save(new Notification(author, recipient, NotificationType.Comment, resourceId));
        messagingTemplate.convertAndSend("/notifications/user/" + recipient.getId() + "/notification", notification);
    }

    public void sendCommentToPost(Long postId, Comment comment) {
        messagingTemplate.convertAndSend("/notifications/post/" + postId + "/likes", comment);
    }
}
