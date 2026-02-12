package com.example.vroom.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.ride.responses.RideResponseDTO;
import com.example.vroom.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRideHistoryViewModel extends ViewModel {
    private final MutableLiveData<List<RideResponseDTO>> rideHistory = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<RideResponseDTO>> getRideHistoryLiveData() {
        return rideHistory;
    }

    public void fetchRideHistory(String email, String sort, String start, String end, int page) {

        RetrofitClient.getAdminService().getRides(email, sort, start, end, page, 10)
                .enqueue(new Callback<List<RideResponseDTO>>() {
                    @Override
                    public void onResponse(Call<List<RideResponseDTO>> call, Response<List<RideResponseDTO>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            rideHistory.setValue(response.body());
                        } else {
                            rideHistory.setValue(null);

                        }
                    }
                    @Override
                    public void onFailure(Call<List<RideResponseDTO>> call, Throwable t) {
                        rideHistory.setValue(null);
                        errorMessage.setValue("Error: " + t.getMessage());
                    }
                });
    }
}