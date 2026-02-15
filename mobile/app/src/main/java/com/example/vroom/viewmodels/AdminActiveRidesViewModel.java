package com.example.vroom.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.ride.responses.GetActiveRideInfoDTO;
import com.example.vroom.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActiveRidesViewModel extends ViewModel {
    private final MutableLiveData<List<GetActiveRideInfoDTO>> rides = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<GetActiveRideInfoDTO>> filteredRides = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<GetActiveRideInfoDTO>> getFilteredRides() {
        return filteredRides;
    }

    public void loadRides() {
        RetrofitClient.getRideService().getAllActiveRides().enqueue(new Callback<List<GetActiveRideInfoDTO>>() {
            @Override
            public void onResponse(Call<List<GetActiveRideInfoDTO>> call, Response<List<GetActiveRideInfoDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    rides.setValue(response.body());
                    filteredRides.setValue(response.body());
                } else {
                    android.util.Log.e("API_ERROR", "Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<GetActiveRideInfoDTO>> call, Throwable t) {
                android.util.Log.e("API_ERROR", "Message: " + t.getMessage());
            }
        });
    }

    public void filter(String query) {
        if (query.isEmpty()) {
            filteredRides.setValue(rides.getValue());
        } else {
            List<GetActiveRideInfoDTO> filtered = rides.getValue().stream()
                    .filter(r -> r.getDriverName().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
            filteredRides.setValue(filtered);
        }
    }
}
