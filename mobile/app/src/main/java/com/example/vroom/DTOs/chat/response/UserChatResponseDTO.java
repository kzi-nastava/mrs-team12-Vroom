package com.example.vroom.DTOs.chat.response;

import java.util.Collection;
import java.util.List;

public class UserChatResponseDTO {
    String userName;
    String profilePicture;
    List<ChatMessageResponseDTO> messages;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public List<ChatMessageResponseDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessageResponseDTO> messages) {
        this.messages = messages;
    }
}
