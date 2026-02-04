package com.example.vroom.services;

import com.example.vroom.DTOs.route.responses.RouteQuoteResponseDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RouteService {
    @GET("api/routes/quote")
    Call<RouteQuoteResponseDTO> getQuote(
            @Query("startLocation") String startLocation,
            @Query("endLocation") String endLocation,
            @Query("stops") String stops
    );

}
