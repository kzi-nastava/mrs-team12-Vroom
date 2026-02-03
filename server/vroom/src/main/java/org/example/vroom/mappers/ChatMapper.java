package org.example.vroom.mappers;

import org.example.vroom.DTOs.requests.chat.ChatMessageRequestDTO;
import org.example.vroom.DTOs.responses.chat.ChatMessageResponseDTO;
import org.example.vroom.DTOs.responses.chat.ChatResponseDTO;
import org.example.vroom.entities.Chat;
import org.example.vroom.entities.Message;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {

    public ChatMessageResponseDTO mapToDTO(Message message) {
        return ChatMessageResponseDTO.builder()
                .content(message.getContent())
                .sentByAdmin(message.isSentByAdmin())
                .timestamp(message.getTimeSent())
                .senderName(message.getSenderName())
                .build();
    };

    public ChatResponseDTO chatToDTO(Chat chat) {
        return ChatResponseDTO.builder()
                .chatId(chat.getId())
                .userName(chat.getUser().getFirstName() + " " + chat.getUser().getLastName())
                .lastMessageTime(chat.getLastMessageTime())
                .build();
    }

    public Message dtoToMessage(ChatMessageRequestDTO dto){
        return Message.builder()
                .content(dto.getContent())
                .sentByAdmin(dto.isSentByAdmin())
                .timeSent(dto.getTimeSent())
                .build();
    }
}
