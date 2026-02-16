package org.example.vroom.DTOs.responses.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.vroom.DTOs.responses.MessageResponseDTO;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ChatResponseDTO {
    private Long chatId;
    private String userName;
    private LocalDateTime lastMessageTime;
    private byte[] profilePicture;
}
