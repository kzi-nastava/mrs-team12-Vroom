package com.example.vroom.services;

import com.example.vroom.DTOs.admin.AdminUserDTO;
import com.example.vroom.DTOs.admin.BlockUserRequestDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AdminService {

    @GET("api/admins/users")
    Call<List<AdminUserDTO>> getAllUsers(@Header("Authorization") String token);

    @PUT("api/admins/users/{id}/block")
    Call<Void> blockUser(
            @Path("id") Long userId,
            @Body BlockUserRequestDTO request,
            @Header("Authorization") String token
    );

    @PUT("api/admins/users/{id}/unblock")
    Call<Void> unblockUser(
            @Path("id") Long userId,
            @Header("Authorization") String token
    );
}