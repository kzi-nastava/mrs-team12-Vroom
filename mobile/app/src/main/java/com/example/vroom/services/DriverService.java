package com.example.vroom.services;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.driver.requests.DriverChangeStatusRequestDTO;
import com.example.vroom.DTOs.ride.responses.RideHistoryMoreInfoResponseDTO;
import com.example.vroom.DTOs.ride.responses.RideHistoryResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DriverService {

    @GET("api/drivers/rides")
    Call<List<RideHistoryResponseDTO>> getRides(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("sort") String sort
    );

    @GET("api/drivers/more-info/{rideID}")
    Call<RideHistoryMoreInfoResponseDTO> getRideMoreInfo(@Path("rideID") Long rideId);

    @PUT("api/drivers/status")
    Call<MessageResponseDTO> changeDriverStatus( @Body DriverChangeStatusRequestDTO data);
}
