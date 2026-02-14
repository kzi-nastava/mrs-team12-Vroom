package com.example.vroom.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.vroom.DTOs.ride.responses.UserActiveRideDTO;
import com.example.vroom.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActiveRideViewModel extends ViewModel {
    private final MutableLiveData<List<UserActiveRideDTO>> activeRides = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<List<UserActiveRideDTO>> getActiveRides() { return activeRides; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadActiveRides() {
        isLoading.setValue(true);
        RetrofitClient.getRideService().getUserActiveRide().enqueue(new Callback<List<UserActiveRideDTO>>() {
            @Override
            public void onResponse(Call<List<UserActiveRideDTO>> call, Response<List<UserActiveRideDTO>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    activeRides.setValue(response.body());
                } else {
                    error.setValue("Failed to fetch rides");
                }
            }

            @Override
            public void onFailure(Call<List<UserActiveRideDTO>> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }
}