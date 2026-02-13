package com.example.vroom.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.ride.requests.FavoriteRouteDTO;
import com.example.vroom.DTOs.ride.requests.OrderFromFavoriteRequestDTO;
import com.example.vroom.DTOs.ride.responses.GetRideResponseDTO;
import com.example.vroom.network.RetrofitClient;
import com.example.vroom.services.RideService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFromFavoritesViewModel extends ViewModel {

    private final MutableLiveData<List<FavoriteRouteDTO>> favoriteRoutes = new MutableLiveData<>();
    private final MutableLiveData<GetRideResponseDTO> orderedRide = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Long> deletedFavoriteId = new MutableLiveData<>();

    private final RideService rideService = RetrofitClient.getRideService();

    public LiveData<List<FavoriteRouteDTO>> getFavoriteRoutes() {
        return favoriteRoutes;
    }

    public LiveData<GetRideResponseDTO> getOrderedRide() {
        return orderedRide;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Long> getDeletedFavoriteId() {
        return deletedFavoriteId;
    }

    public void loadFavoriteRoutes() {
        isLoading.setValue(true);

        rideService.getFavoriteRoutes().enqueue(new Callback<List<FavoriteRouteDTO>>() {
            @Override
            public void onResponse(Call<List<FavoriteRouteDTO>> call, Response<List<FavoriteRouteDTO>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    favoriteRoutes.setValue(response.body());
                } else {
                    error.setValue("Failed to load favorite routes");
                }
            }

            @Override
            public void onFailure(Call<List<FavoriteRouteDTO>> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void orderFromFavorite(OrderFromFavoriteRequestDTO request) {
        isLoading.setValue(true);

        rideService.orderFromFavorite(request).enqueue(new Callback<GetRideResponseDTO>() {
            @Override
            public void onResponse(Call<GetRideResponseDTO> call, Response<GetRideResponseDTO> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    orderedRide.setValue(response.body());
                } else if (response.code() == 400) {
                    error.setValue("Invalid request or scheduling conflict");
                } else {
                    error.setValue("Failed to order ride: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GetRideResponseDTO> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void removeFavorite(Long favoriteId) {
        isLoading.setValue(true);

        rideService.removeFavorite(favoriteId).enqueue(new Callback<MessageResponseDTO>() {
            @Override
            public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    deletedFavoriteId.setValue(favoriteId);
                    loadFavoriteRoutes();
                } else {
                    error.setValue("Failed to delete favorite route");
                }
            }

            @Override
            public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
            }
        });
    }
}