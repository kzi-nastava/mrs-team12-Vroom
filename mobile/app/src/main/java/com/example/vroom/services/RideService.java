package com.example.vroom.services;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.ride.requests.CancelRideRequestDTO;
import com.example.vroom.DTOs.ride.requests.ComplaintRequestDTO;
import com.example.vroom.DTOs.ride.requests.StopRideRequestDTO;
import com.example.vroom.DTOs.ride.responses.RideHistoryResponseDTO;
import com.example.vroom.DTOs.ride.responses.StoppedRideResponseDTO;
import com.example.vroom.DTOs.ride.responses.UserActiveRideDTO;
import com.example.vroom.DTOs.route.responses.GetRouteResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RideService {

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

    @GET("api/rides/user-active-ride")
    Call<List<UserActiveRideDTO>> getUserActiveRide();

    @GET("api/rides/route/{rideID}")
    Call<GetRouteResponseDTO> getRoute(
            @Path("rideID") Long rideID
    );

    @POST("api/rides/complaint/{rideID}")
    Call<MessageResponseDTO> sendComplaint(
            @Path("rideID") Long rideID,
            @Body ComplaintRequestDTO complaint
    );

    @POST("api/rides/{rideID}/finish")
    Call<MessageResponseDTO> finishRide(
            @Path("rideID") Long rideID
    );
}
