package com.example.vroom.DTOs.chat.request;

import java.time.LocalDateTime;

public class ChatMessageRequestDTO {
    private String timeSent;
    private boolean sentByAdmin;
    private String content;

    public ChatMessageRequestDTO() {
    }

    public ChatMessageRequestDTO(String timeSent, boolean sentByAdmin, String content) {
        this.timeSent = timeSent;
        this.sentByAdmin = sentByAdmin;
        this.content = content;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }

    public boolean isSentByAdmin() {
        return sentByAdmin;
    }

    public void setSentByAdmin(boolean sentByAdmin) {
        this.sentByAdmin = sentByAdmin;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
