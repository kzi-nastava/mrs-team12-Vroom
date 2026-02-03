package org.example.vroom.controllers;

import org.example.vroom.DTOs.requests.chat.ChatMessageRequestDTO;
import org.example.vroom.DTOs.responses.MessageResponseDTO;
import org.example.vroom.DTOs.responses.chat.ChatMessageResponseDTO;
import org.example.vroom.DTOs.responses.chat.ChatResponseDTO;
import org.example.vroom.entities.User;
import org.example.vroom.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping(path = "/get-chat/{userId}")
    public ResponseEntity<Collection<ChatMessageResponseDTO>> getMessages(
            @PathVariable Long userId
    ) {
        List<ChatMessageResponseDTO> messages = chatService.getMessagesByUserId(userId);
        if (messages.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping(path="/get-all-chats")
    public ResponseEntity<Collection<ChatResponseDTO>> getAllChats(
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<ChatResponseDTO> chats = chatService.getChatsByAdministrator(user.getId());
        if (chats.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(chats, HttpStatus.OK);
    }

    @MessageMapping("send-message/{userId}")
    public void sendMessage(
            @DestinationVariable String userId,
            ChatMessageRequestDTO messageRequestDTO
    ) {
        ChatMessageResponseDTO messageResponseDTO = chatService.sendMessage(Long.valueOf(userId), messageRequestDTO);
        messagingTemplate.convertAndSend("/socket-publisher/send-message/" + userId, messageResponseDTO);
    }

}
