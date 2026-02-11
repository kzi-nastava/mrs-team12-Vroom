package org.example.vroom.DTOs.responses.chat;

import lombok.*;

import java.util.Collection;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserChatResponseDTO {
    String userName;
    byte[] profilePicture;
    Collection<ChatMessageResponseDTO> messages;
}
