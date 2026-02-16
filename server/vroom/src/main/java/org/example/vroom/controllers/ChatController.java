package org.example.vroom.controllers;

import org.example.vroom.DTOs.requests.chat.ChatMessageRequestDTO;
import org.example.vroom.DTOs.responses.MessageResponseDTO;
import org.example.vroom.DTOs.responses.chat.ChatMessageResponseDTO;
import org.example.vroom.DTOs.responses.chat.ChatResponseDTO;
import org.example.vroom.DTOs.responses.chat.UserChatResponseDTO;
import org.example.vroom.entities.User;
import org.example.vroom.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/get-admin-chat/{chatID}")
    public ResponseEntity<Collection<ChatMessageResponseDTO>> getAdminChat(
            @PathVariable Long chatID
    ) {
        List<ChatMessageResponseDTO> messages = chatService.getMessagesByChatId(chatID);
        if (messages.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

//    @PreAuthorize("hasAnyRole('REGISTERED_USER', 'DRIVER')")
    @GetMapping(path="/get-user-chat")
    public ResponseEntity<UserChatResponseDTO> getUserChat(
            @AuthenticationPrincipal User user
    ){
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        UserChatResponseDTO responseDTO = chatService.getMessagesByUserId(user.getId());
        if (responseDTO.getMessages().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path="/get-all-chats")
    public ResponseEntity<Collection<ChatResponseDTO>> getAllChats(
    ) {
        List<ChatResponseDTO> chats = chatService.getAllChats();
        for (ChatResponseDTO chat : chats){
            System.out.println(chat.getUserName());
        }
        if (chats.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(chats, HttpStatus.OK);
    }

//    @PreAuthorize("hasAnyRole('REGISTERED_USER', 'DRIVER')")
    @MessageMapping("user-send-message")
    @SendTo("/socket-publisher/user-messages")
    public ChatMessageResponseDTO userSendMessage(
            SimpMessageHeaderAccessor headerAccessor,
            ChatMessageRequestDTO messageRequestDTO
    ) {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) headerAccessor.getUser();
        if(auth == null || auth.getPrincipal() == null) {
            return null;
        }
        User user = (User) auth.getPrincipal();
        return chatService.userSendMessage(user.getId(), messageRequestDTO);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @MessageMapping("admin-send-message/{chatID}")
    public void adminSendMessage(
            @DestinationVariable Long chatID,
            SimpMessageHeaderAccessor headerAccessor,
            ChatMessageRequestDTO messageRequestDTO
    ){
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) headerAccessor.getUser();
        if(auth == null || auth.getPrincipal() == null) {
            return;
        }
        User admin = (User) auth.getPrincipal();
        Long userID = chatService.getUserId(chatID);
        ChatMessageResponseDTO messageResponseDTO = chatService.adminSendMessage(admin, chatID, messageRequestDTO);
        messagingTemplate.convertAndSend("/socket-publisher/admin-messages/" + userID, messageResponseDTO);
    }

}
