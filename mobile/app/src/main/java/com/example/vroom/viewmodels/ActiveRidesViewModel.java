package com.example.vroom.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.ride.responses.GetRideResponseDTO;
import com.example.vroom.network.RetrofitClient;
import com.example.vroom.services.RideService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveRidesViewModel extends ViewModel {

    private final MutableLiveData<List<GetRideResponseDTO>> rides = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private final RideService rideService = RetrofitClient.getRideService();

    public LiveData<List<GetRideResponseDTO>> getRides() {
        return rides;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void fetchActiveRides() {
        isLoading.setValue(true);

        rideService.getActiveRides().enqueue(new Callback<List<GetRideResponseDTO>>() {
            @Override
            public void onResponse(Call<List<GetRideResponseDTO>> call, Response<List<GetRideResponseDTO>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    rides.setValue(response.body());
                } else if (response.code() == 204) {
                    rides.setValue(List.of());
                } else {
                    error.setValue("Error loading rides: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<GetRideResponseDTO>> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void startRide(Long rideId) {
        isLoading.setValue(true);

        rideService.startRide(rideId).enqueue(new Callback<GetRideResponseDTO>() {
            @Override
            public void onResponse(Call<GetRideResponseDTO> call, Response<GetRideResponseDTO> response) {
                isLoading.setValue(false);

                if (response.isSuccessful()) {
                    fetchActiveRides();
                } else {
                    error.setValue("Failed to start ride: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GetRideResponseDTO> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
            }
        });
    }
}