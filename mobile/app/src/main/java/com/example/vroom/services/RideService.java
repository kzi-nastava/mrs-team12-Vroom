package com.example.vroom.services;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.ride.requests.CancelRideRequestDTO;
import com.example.vroom.DTOs.ride.requests.StopRideRequestDTO;
import com.example.vroom.DTOs.ride.responses.RideHistoryResponseDTO;
import com.example.vroom.DTOs.ride.responses.StoppedRideResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RideService {

    @GET("api/drivers/rides")
    Call<List<RideHistoryResponseDTO>> getRides(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("sort") String sort
    );

    @PUT("api/rides/{rideID}/cancel")
    Call<MessageResponseDTO> cancelRide(
            @Path("rideID") Long rideId,
            @Body CancelRideRequestDTO data
    );

    @PUT("api/rides/{rideID}/stop")
    Call<StoppedRideResponseDTO> stopRide(
            @Path("rideID") Long rideId,
            @Body StopRideRequestDTO data
    );
}
