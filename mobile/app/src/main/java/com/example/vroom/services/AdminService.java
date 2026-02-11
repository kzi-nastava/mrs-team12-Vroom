package com.example.vroom.services;

import com.example.vroom.DTOs.ride.responses.RideResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AdminService {
    @GET("api/admins/users/rides")
    Call<List<RideResponseDTO>> getRides(
            @Query("userEmail") String userEmail,
            @Query("sort") String sort,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("pageNumber") int pageNumber,
            @Query("pageSize") int pageSize
    );
}
