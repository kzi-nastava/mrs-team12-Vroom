package com.example.vroom.viewmodels;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.ride.requests.CancelRideRequestDTO;
import com.example.vroom.DTOs.ride.requests.StopRideRequestDTO;
import com.example.vroom.DTOs.ride.responses.StoppedRideResponseDTO;
import com.example.vroom.network.RetrofitClient;

import java.time.LocalDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CancelRideViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<MessageResponseDTO> cancelSuccess = new MutableLiveData<>();

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public MutableLiveData<MessageResponseDTO> getCancelSuccess() { return cancelSuccess; }
    public MutableLiveData<String> getErrorMessage() { return errorMessage; }

    private CancelRideRequestDTO createCancelRideRequest(@Nullable String reason){
        CancelRideRequestDTO req = new CancelRideRequestDTO();

        if(reason != null)
            req.setReason(reason);

        return req;
    }


    public void cancelRide(Long rideID, @Nullable String reason) {
        isLoading.setValue(true);

        CancelRideRequestDTO req = createCancelRideRequest(reason);

        RetrofitClient.getRideService().cancelRide(rideID, req).enqueue(new Callback<MessageResponseDTO>() {
            @Override
            public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    cancelSuccess.setValue(response.body());
                } else {
                    errorMessage.setValue("Error " + response.code() + ": Failed to cancel the ride.");
                }
            }

            @Override
            public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }
}