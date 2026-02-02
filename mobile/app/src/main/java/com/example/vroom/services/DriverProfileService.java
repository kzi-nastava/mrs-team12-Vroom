package com.example.vroom.services;

import com.example.vroom.DTOs.driver.requests.DriverDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface DriverProfileService {
    @GET("/api/profile/driver/me")
    Call<DriverDTO> getMyProfile();

    @PUT("/api/profile/driver/me")
    Call<Void> requestUpdate(@Body DriverDTO dto);
}
