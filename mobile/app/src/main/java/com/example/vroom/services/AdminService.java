package com.example.vroom.services;

import com.example.vroom.DTOs.admin.AdminUserDTO;
import com.example.vroom.DTOs.admin.BlockUserRequestDTO;
import com.example.vroom.DTOs.admin.DriverRegistrationRequestDTO;
import com.example.vroom.DTOs.admin.DriverUpdateDTO;
import com.example.vroom.DTOs.admin.DriverUpdateRequestAdminDTO;
import com.example.vroom.DTOs.admin.RejectRequestDTO;
import com.example.vroom.DTOs.driver.requests.DriverDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
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

    @GET("api/admins/driver-update-requests")
    Call<List<DriverUpdateRequestAdminDTO>> getPendingRequests(@Header("Authorization") String token);

    @POST("api/admins/driver-update-requests/{id}/approve")
    Call<DriverUpdateDTO> approveRequest(
            @Path("id") Long requestId,
            @Header("Authorization") String token
    );

    @POST("api/admins/driver-update-requests/{id}/reject")
    Call<Void> rejectRequest(
            @Path("id") Long requestId,
            @Body RejectRequestDTO request,
            @Header("Authorization") String token
    );
    @POST("api/drivers/register/driver")
    Call<DriverDTO> registerDriver(
            @Body DriverRegistrationRequestDTO request,
            @Header("Authorization") String token
    );
}