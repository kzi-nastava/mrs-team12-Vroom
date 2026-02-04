package com.example.vroom.services;

import com.example.vroom.DTOs.geocode.responses.AddressSuggestionResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeoLocationService {
    @GET("api/geo/autocomplete-address")
    Call<List<AddressSuggestionResponseDTO>> geolocateMultipleAddresses(
            @Query("location") String location
    );

    @GET("api/geo/geocode-address")
    Call<AddressSuggestionResponseDTO> geolocateAddress(
            @Query("location") String location
    );
}
