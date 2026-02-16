package com.example.vroom.services;

import android.annotation.SuppressLint;
import android.os.Looper;

import com.example.vroom.DTOs.chat.response.ChatMessageResponseDTO;
import com.example.vroom.DTOs.chat.response.ChatResponseDTO;
import com.example.vroom.DTOs.chat.response.UserChatResponseDTO;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ChatService {

    @GET("/api/chat/get-admin-chat/{chatID}")
    Call<List<ChatMessageResponseDTO>> getAdminChat(
            @Path("chatID") Long chatID
    );

    @GET("/api/chat/get-user-chat")
    Call<UserChatResponseDTO> getUserChat();

    @GET("/api/chat/get-all-chats")
    Call<List<ChatResponseDTO>> getAllChats();


}
