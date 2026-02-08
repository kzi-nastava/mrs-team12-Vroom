package com.example.vroom.services;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.panic.requests.PanicRequestDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PanicNotificationService {
    @POST("/api/panics")
    Call<MessageResponseDTO> createPanic(
            @Body PanicRequestDTO data
    );
}
