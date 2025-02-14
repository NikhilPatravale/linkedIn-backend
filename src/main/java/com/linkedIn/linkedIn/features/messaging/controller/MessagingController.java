package com.linkedIn.linkedIn.features.messaging.controller;

import com.linkedIn.linkedIn.features.authentication.model.AuthenticationUser;
import com.linkedIn.linkedIn.features.messaging.dto.MessageDto;
import com.linkedIn.linkedIn.features.messaging.model.Conversation;
import com.linkedIn.linkedIn.features.messaging.service.MessagingService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/v1/messaging")
public class MessagingController {

    private final MessagingService messagingService;

    public MessagingController(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<Conversation>> getAllConversations(@RequestAttribute("authenticatedUser")AuthenticationUser user) {
        return ResponseEntity.ok(messagingService.getAllConversations(user));
    }

    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<Conversation> getConversation(@RequestAttribute("authenticatedUser")AuthenticationUser user, @PathVariable Long conversationId) {
        return ResponseEntity.ok(messagingService.getConversation(user, conversationId));
    }

    @PostMapping("/conversations")
    public ResponseEntity<Conversation> createConversationAndAddMessage(@RequestAttribute("authenticatedUser") AuthenticationUser user, @RequestBody MessageDto messageDto) {
        return ResponseEntity.ok(messagingService.createConversationAndAddMessage(user.getId(), messageDto.getReceiverId(), messageDto.getContent()));
    }

    @PostMapping("/conversations/{conversationId}/messaging")
    public ResponseEntity<Conversation> addMessageToConversation(@RequestAttribute("authenticatedUser") AuthenticationUser user, @PathVariable Long conversationId, @RequestBody MessageDto messageDto) {
        return ResponseEntity.ok(messagingService.addMessageToConversation(user, conversationId,  messageDto.getContent()));
    }

    @PutMapping("/conversations/messages/{messageId}")
    public ResponseEntity<Void> markMessageRead(@RequestAttribute("authenticatedUser") AuthenticationUser user, @PathVariable Long messageId) {
        return ResponseEntity.ok(messagingService.markMessageRead(user, messageId));
    }
}
