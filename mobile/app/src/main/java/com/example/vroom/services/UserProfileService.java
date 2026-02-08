package com.example.vroom.services;
import com.example.vroom.DTOs.auth.requests.ChangePasswordRequestDTO;
import com.example.vroom.DTOs.registeredUser.RegisteredUserDTO;
import com.example.vroom.DTOs.registeredUser.UpdateProfileRequestDTO;

import retrofit2.http.Body;
import retrofit2.http.GET;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;

public interface UserProfileService {
    @GET("/api/profile/user/me")
    Call<RegisteredUserDTO> getMyProfile();

    @PUT("/api/profile/user/me")
    Call<Void> updateProfile(@Body UpdateProfileRequestDTO dto);

    @PUT("/api/profile/user/change-password")
    Call<String> changePassword(@Body ChangePasswordRequestDTO dto);
}
