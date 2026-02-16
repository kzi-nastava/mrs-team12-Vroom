package com.example.vroom.DTOs.chat.response;

import java.time.LocalDateTime;

public class ChatResponseDTO {
    private Long chatId;
    private String userName;
    private String profilePicture;
    private LocalDateTime lastMessageTime;

    public ChatResponseDTO(Long chatId, String userName, LocalDateTime lastMessageTime, String profilePicture) {
        this.chatId = chatId;
        this.userName = userName;
        this.lastMessageTime = lastMessageTime;
        this.profilePicture = profilePicture;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public ChatResponseDTO() {
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
