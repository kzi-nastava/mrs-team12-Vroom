package com.example.vroom.services;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.auth.requests.ForgotPasswordRequestDTO;
import com.example.vroom.DTOs.auth.requests.LoginRequestDTO;
import com.example.vroom.DTOs.auth.requests.LogoutRequestDTO;
import com.example.vroom.DTOs.auth.requests.RegisterUserRequestDTO;
import com.example.vroom.DTOs.auth.requests.ResetPasswordRequestDTO;
import com.example.vroom.DTOs.auth.responses.LoginResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;


public interface AuthService {
    @POST("api/auth/login")
    Call<LoginResponseDTO> login(@Body LoginRequestDTO data);
    @POST("api/auth/forgot-password")
    Call<MessageResponseDTO> forgotPassword(@Body ForgotPasswordRequestDTO data);
    @PUT("api/auth/reset-password")
    Call<MessageResponseDTO> resetPassword(@Body ResetPasswordRequestDTO data);
    @POST("api/auth/register")
    Call<MessageResponseDTO> registerUser(@Body RegisterUserRequestDTO user);
    @POST("api/auth/logout")
    Call<MessageResponseDTO> logout(@Body LogoutRequestDTO data);
}
