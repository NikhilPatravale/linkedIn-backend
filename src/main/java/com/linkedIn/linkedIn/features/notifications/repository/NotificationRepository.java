package com.linkedIn.linkedIn.features.notifications.repository;

import com.linkedIn.linkedIn.features.authentication.model.AuthenticationUser;
import com.linkedIn.linkedIn.features.notifications.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    public List<Notification> findByRecipient(AuthenticationUser recipient);
    public List<Notification> findByRecipientOrderByCreationDateTimeDesc(AuthenticationUser recipient);
}
