package com.supervision.livraisons.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supervision.livraisons.dto.SendMessageRequest;
import com.supervision.livraisons.model.Message;
import com.supervision.livraisons.service.MessageService;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/conversations")
    public List<Map<String, Object>> getConversations(Authentication authentication) {
        return messageService.getConversations(currentUserId(authentication));
    }

    @GetMapping("/{userId}")
    public List<Message> getThread(@PathVariable String userId, Authentication authentication) {
        return messageService.getThread(currentUserId(authentication), userId);
    }

    @PostMapping
    public Message sendMessage(@RequestBody SendMessageRequest request, Authentication authentication) {
        return messageService.sendMessage(currentUserId(authentication), request);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id, Authentication authentication) {
        messageService.markAsRead(id, currentUserId(authentication));
        return ResponseEntity.noContent().build();
    }

    private String currentUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return authentication.getName();
    }
}
