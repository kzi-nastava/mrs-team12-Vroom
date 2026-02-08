package com.example.vroom.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.ride.responses.RideHistoryMoreInfoResponseDTO;
import com.example.vroom.DTOs.ride.responses.RideHistoryResponseDTO;
import com.example.vroom.network.RetrofitClient;
import com.example.vroom.services.DriverService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideHistoryViewModel extends ViewModel {
    private final MutableLiveData<List<RideHistoryResponseDTO>> rides = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<RideHistoryMoreInfoResponseDTO> selectedRide = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final DriverService driverService = RetrofitClient.getDriverService();

    public LiveData<List<RideHistoryResponseDTO>> getRides(){
        return rides;
    }

    public LiveData<String> getErrorMessage(){
        return errorMessage;
    }

    public LiveData<RideHistoryMoreInfoResponseDTO> getSelectedRide() {
        return selectedRide;
    }

    public void fetchRideHistory(String sort) {
        driverService.getRides(null, null, sort).enqueue(new Callback<List<RideHistoryResponseDTO>>() {
            @Override
            public void onResponse(Call<List<RideHistoryResponseDTO>> call, Response<List<RideHistoryResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null){
                    rides.setValue(response.body());
                }else if (response.code() == 204) {
                    errorMessage.setValue("No rides Found.");
                }else{
                    errorMessage.setValue("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<RideHistoryResponseDTO>> call, Throwable t) {
                errorMessage.setValue(t.getMessage());
            }
        });
    }

    public void fetchRideDetails(Long rideId) {
        isLoading.setValue(true);
        driverService.getRideMoreInfo(rideId).enqueue(new Callback<RideHistoryMoreInfoResponseDTO>() {
            @Override
            public void onResponse(Call<RideHistoryMoreInfoResponseDTO> call, Response<RideHistoryMoreInfoResponseDTO> response) {
                isLoading.setValue(false);
                if(response.isSuccessful()){
                    selectedRide.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<RideHistoryMoreInfoResponseDTO> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
    }

    public void clearSelectedRide(){
        selectedRide.setValue(null);
    }
}
