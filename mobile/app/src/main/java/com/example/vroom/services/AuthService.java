package com.example.vroom.services;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.auth.requests.ForgotPasswordRequestDTO;
import com.example.vroom.DTOs.auth.requests.LoginRequestDTO;
import com.example.vroom.DTOs.auth.requests.LogoutRequestDTO;
import com.example.vroom.DTOs.auth.requests.RegisterUserRequestDTO;
import com.example.vroom.DTOs.auth.requests.ResetPasswordRequestDTO;
import com.example.vroom.DTOs.auth.responses.LoginResponseDTO;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;


public interface AuthService {
    @POST("api/auth/login")
    Call<LoginResponseDTO> login(@Body LoginRequestDTO data);
    @POST("api/auth/forgot-password")
    Call<MessageResponseDTO> forgotPassword(@Body ForgotPasswordRequestDTO data);
    @PUT("api/auth/reset-password")
    Call<MessageResponseDTO> resetPassword(@Body ResetPasswordRequestDTO data);

    @POST("api/auth/logout")
    Call<MessageResponseDTO> logout(@Body LogoutRequestDTO data);

    @Multipart
    @POST("api/auth/register")
    Call<MessageResponseDTO> registerUser(
            @Part("firstName") RequestBody firstName,
            @Part("lastName") RequestBody lastName,
            @Part("email") RequestBody email,
            @Part("phoneNumber") RequestBody phone,
            @Part("address") RequestBody address,
            @Part("gender") RequestBody gender,
            @Part("password") RequestBody password,
            @Part("confirmPassword") RequestBody confirmPassword,
            @Part MultipartBody.Part profilePhoto
    );
}
