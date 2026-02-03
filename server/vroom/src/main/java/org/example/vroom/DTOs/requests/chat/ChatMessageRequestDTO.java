package org.example.vroom.DTOs.requests.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequestDTO {
    private LocalDateTime timeSent;
    private boolean sentByAdmin;
    private String content;
}
