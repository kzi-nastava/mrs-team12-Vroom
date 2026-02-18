package com.example.vroom.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.map.MapRouteDTO;
import com.example.vroom.DTOs.ride.responses.CreateFavoriteRouteRequestDTO;
import com.example.vroom.DTOs.ride.responses.FavoriteRouteResponseDTO;
import com.example.vroom.DTOs.ride.responses.RideResponseDTO;
import com.example.vroom.DTOs.route.responses.GetRouteResponseDTO;
import com.example.vroom.DTOs.route.responses.PointResponseDTO;
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

    private final MutableLiveData<MapRouteDTO> mapDrawingData = new MutableLiveData<>();
    public LiveData<MapRouteDTO> getRoute() {
        return mapDrawingData;
    }
    public void sendRideData(RideResponseDTO ride){
        MapRouteDTO route = convertRouteToMap(ride.getRoute());
        mapDrawingData.setValue(route);
    }

    private MapRouteDTO convertRouteToMap(GetRouteResponseDTO route){
        MapRouteDTO data = new MapRouteDTO();

        data.setStart(new PointResponseDTO(route.getStartLocationLat(), route.getStartLocationLng()));
        data.setStops(route.getStops());
        data.setEnd(new PointResponseDTO(route.getEndLocationLat(), route.getEndLocationLng()));

        return data;
    }

    public void fetchRideHistoryUser(String sort, String start, String end, int page) {

        RetrofitClient.getRegisteredUserService().getRides(sort, start, end, page, 10)
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
    public void fetchRideHistoryAdmin(String email, String sort, String start, String end, int page) {

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

    private final MutableLiveData<String> favoriteSuccess = new MutableLiveData<>();

    public LiveData<String> getFavoriteSuccess() {
        return favoriteSuccess;
    }

    public void addFavoriteRoute(Long rideId, String name) {
        CreateFavoriteRouteRequestDTO request = new CreateFavoriteRouteRequestDTO(rideId, name);

        RetrofitClient.getRegisteredUserService().addFavoriteRoute(request)
                .enqueue(new Callback<FavoriteRouteResponseDTO>() {
                    @Override
                    public void onResponse(Call<FavoriteRouteResponseDTO> call, Response<FavoriteRouteResponseDTO> response) {
                        if (response.isSuccessful()) {
                            favoriteSuccess.setValue("Route added to favorites!");
                        } else if (response.code() == 400) {
                            errorMessage.setValue("This route is already in your favorites");
                        } else if (response.code() == 404) {
                            errorMessage.setValue("Ride not found");
                        } else {
                          
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                                Log.e("FAVORITE", "Code: " + response.code() + " Body: " + errorBody);
                            } catch (Exception e) {
                                Log.e("FAVORITE", "Code: " + response.code());
                            }
                            errorMessage.setValue("Failed to add to favorites: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<FavoriteRouteResponseDTO> call, Throwable t) {
                        errorMessage.setValue("Network error: " + t.getMessage());
                    }
                });
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}