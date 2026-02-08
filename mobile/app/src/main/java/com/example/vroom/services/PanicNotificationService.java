package com.example.vroom.services;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.panic.requests.PanicRequestDTO;
import com.example.vroom.DTOs.panic.responses.PanicNotificationResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PanicNotificationService {
    @GET("api/panics")
    Call<List<PanicNotificationResponseDTO>> getPanicNotifications(
            @Query("active") boolean active
    );
    @POST("api/panics")
    Call<MessageResponseDTO> createPanic(
            @Body PanicRequestDTO data
    );

    @PUT("api/panics/{panicID}/resolve")
    Call<MessageResponseDTO> resolvePanic(
            @Path("panicID") Long panicID
    );
}
