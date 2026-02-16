package com.example.vroom.DTOs.chat.response;

import java.time.LocalDateTime;

public class ChatMessageResponseDTO {
    private Long chatID;
    private String senderName;
    private String content;
    private String timestamp;
    private boolean sentByAdmin;
    private String profilePicture;

    public ChatMessageResponseDTO(Long chatID, String senderName, String content, String timestamp, boolean sentByAdmin, String profilePicture) {
        this.chatID = chatID;
        this.senderName = senderName;
        this.content = content;
        this.timestamp = timestamp;
        this.sentByAdmin = sentByAdmin;
        this.profilePicture = profilePicture;
    }

    public ChatMessageResponseDTO() {
    }

    public Long getChatID() {
        return chatID;
    }

    public void setChatID(Long chatID) {
        this.chatID = chatID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSentByAdmin() {
        return sentByAdmin;
    }

    public void setSentByAdmin(boolean sentByAdmin) {
        this.sentByAdmin = sentByAdmin;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
