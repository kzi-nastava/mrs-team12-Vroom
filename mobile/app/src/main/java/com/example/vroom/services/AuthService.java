package com.example.vroom.services;

import com.example.vroom.DTOs.MessageResponse;
import com.example.vroom.DTOs.auth.requests.RegisterUserRequestDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface AuthService {
    @POST("api/auth/register")
    Call<MessageResponse> registerUser(@Body RegisterUserRequestDTO user);
}
