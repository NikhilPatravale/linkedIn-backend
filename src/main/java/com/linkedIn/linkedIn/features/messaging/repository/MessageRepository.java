package com.linkedIn.linkedIn.features.messaging.repository;

import com.linkedIn.linkedIn.features.messaging.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
