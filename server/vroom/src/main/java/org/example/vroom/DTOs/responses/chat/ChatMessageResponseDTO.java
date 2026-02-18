package org.example.vroom.DTOs.responses.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ChatMessageResponseDTO {
    private Long chatID;
    private String senderName;
    private String content;
    private LocalDateTime timestamp;
    private boolean sentByAdmin;
    private byte[] profilePicture;
}
