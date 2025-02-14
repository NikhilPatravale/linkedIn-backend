package com.linkedIn.linkedIn.features.messaging.service;

import com.linkedIn.linkedIn.features.authentication.model.AuthenticationUser;
import com.linkedIn.linkedIn.features.authentication.service.AuthenticationUserService;
import com.linkedIn.linkedIn.features.messaging.model.Conversation;
import com.linkedIn.linkedIn.features.messaging.model.Message;
import com.linkedIn.linkedIn.features.messaging.repository.ConversationRepository;
import com.linkedIn.linkedIn.features.messaging.repository.MessageRepository;
import com.linkedIn.linkedIn.features.notifications.services.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessagingService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AuthenticationUserService authenticationUserService;
    private final NotificationService notificationService;

    public MessagingService(ConversationRepository conversationRepository, MessageRepository messageRepository, AuthenticationUserService authenticationUserService, NotificationService notificationService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.authenticationUserService = authenticationUserService;
        this.notificationService = notificationService;
    }

    public List<Conversation> getAllConversations(AuthenticationUser user) {
        return conversationRepository.findByAuthorOrRecipient(user, user);
    }

    public Conversation getConversation(AuthenticationUser user, Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId).orElseThrow(() -> new IllegalArgumentException("Conversation not found"));
        if (!conversation.getAuthor().getId().equals(user.getId()) && !conversation.getRecipient().getId().equals(user.getId())) {
            throw new IllegalArgumentException("User are not authorised to read this conversation");
        }
        return conversation;
    }

    @Transactional
    public Conversation createConversationAndAddMessage(Long senderId, Long receiverId, String content) {
        AuthenticationUser author = authenticationUserService.getUserById(senderId);
        AuthenticationUser recipient = authenticationUserService.getUserById(receiverId);

        conversationRepository.findByAuthorAndRecipient(author, recipient).ifPresentOrElse(
                conversation -> {
                    throw new IllegalArgumentException("Conversation already exist, use conversationId to send message");
                },
                () -> {}
        );
        conversationRepository.findByAuthorAndRecipient(recipient, author).ifPresentOrElse(
                conversation -> {
                    throw new IllegalArgumentException("Conversation already exist, use conversationId to send message");
                },
                () -> {}
        );

        Conversation conversation = conversationRepository.save(new Conversation(author, recipient));
        Message message = messageRepository.save(new Message(author, recipient, conversation, content));
        conversation.getMessages().add(message);
        notificationService.sendMessageToAuthorAndRecipient(author.getId(), recipient.getId(), message);
        return conversation;
    }

    public Conversation addMessageToConversation(AuthenticationUser sender, Long conversationId, String content) {
        Conversation conversation = conversationRepository.findById(conversationId).orElseThrow(() -> new IllegalArgumentException("Conversation not found"));
        if (!conversation.getAuthor().getId().equals(sender.getId()) && !conversation.getRecipient().getId().equals(sender.getId())) {
            throw new IllegalArgumentException("User is not authorised to send message to this conversation");
        }
        AuthenticationUser receiver = conversation.getAuthor().getId().equals(sender.getId())
                ? conversation.getRecipient() : conversation.getAuthor();
        Message message = new Message(sender, receiver, conversation, content);
        messageRepository.save(message);
        conversation.getMessages().add(message);
        notificationService.sendMessageToConversation(conversation.getId(), message);
        notificationService.sendMessageToAuthorAndRecipient(sender.getId(), receiver.getId(), message);
        return conversation;
    }

    public Void markMessageRead(AuthenticationUser user, Long messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new IllegalArgumentException("Message not found"));

        if (!message.getReceiver().getId().equals(user.getId())) {
            throw new IllegalArgumentException("User is not receiver of this message");
        }
        message.setRead(true);
        notificationService.sendMessageToConversation(message.getConversation().getId(), messageRepository.save(message));
        return null;
    }
}
