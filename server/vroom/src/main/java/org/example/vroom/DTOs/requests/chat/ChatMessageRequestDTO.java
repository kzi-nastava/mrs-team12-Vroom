package org.example.vroom.DTOs.requests.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Time cannot be null")
    private LocalDateTime timeSent;

    private boolean sentByAdmin;

    @NotBlank(message = "Message cannot be blank")
    private String content;
}