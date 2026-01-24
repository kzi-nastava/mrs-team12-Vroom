package com.example.vroom.services;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.driver.requests.DriverChangeStatusRequestDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface DriverService {
    @PUT("api/drivers/status")
    Call<MessageResponseDTO> changeDriverStatus( @Body DriverChangeStatusRequestDTO data);
}
