package com.linkedIn.linkedIn.features.notifications.model;

import com.linkedIn.linkedIn.features.authentication.model.AuthenticationUser;
import com.linkedIn.linkedIn.features.notifications.dto.NotificationType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    public AuthenticationUser actor;

    @ManyToOne
    public AuthenticationUser recipient;

    public boolean isRead;
    public NotificationType notificationType;
    public Long resourceId;

    @CreationTimestamp
    public String creationDateTime;

    public Notification(AuthenticationUser actor, AuthenticationUser recipient, NotificationType notificationType, Long resourceId) {
        this.actor = actor;
        this.recipient = recipient;
        this.notificationType = notificationType;
        this.resourceId = resourceId;
    }

    public AuthenticationUser getActor() {
        return actor;
    }

    public void setActor(AuthenticationUser actor) {
        this.actor = actor;
    }

    public AuthenticationUser getRecipient() {
        return recipient;
    }

    public void setRecipient(AuthenticationUser recipient) {
        this.recipient = recipient;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }
}
