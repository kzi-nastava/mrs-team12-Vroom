package com.example.vroom.services;

import com.example.vroom.DTOs.ride.responses.RideHistoryResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RideService {

    @GET("api/drivers/rides")
    Call<List<RideHistoryResponseDTO>> getRides(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("sort") String sort
    );
}
