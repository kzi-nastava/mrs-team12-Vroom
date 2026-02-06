package com.example.vroom.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.ride.requests.StopRideRequestDTO;
import com.example.vroom.DTOs.ride.responses.StoppedRideResponseDTO;
import com.example.vroom.network.RetrofitClient;

import java.time.LocalDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StopRideViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<StoppedRideResponseDTO> stopSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public MutableLiveData<StoppedRideResponseDTO> getStopSuccess() { return stopSuccess; }
    public MutableLiveData<String> getErrorMessage() { return errorMessage; }

    private StopRideRequestDTO createStopRideRequest(double lat, double lng){
        StopRideRequestDTO req = new StopRideRequestDTO();

        req.setEndTime(LocalDateTime.now());
        req.setStopLat(lat);
        req.setStopLng(lng);

        return req;
    }
    public void stopRideWithLocation(Long rideID, double lat, double lng) {
        isLoading.setValue(true);

        StopRideRequestDTO req = createStopRideRequest(lat, lng);

        RetrofitClient.getRideService().stopRide(rideID, req).enqueue(new Callback<StoppedRideResponseDTO>() {
            @Override
            public void onResponse(Call<StoppedRideResponseDTO> call, Response<StoppedRideResponseDTO> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    stopSuccess.setValue(response.body());
                } else {
                    errorMessage.setValue("Error " + response.code() + ": Failed to stop ride.");
                }
            }

            @Override
            public void onFailure(Call<StoppedRideResponseDTO> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }


}