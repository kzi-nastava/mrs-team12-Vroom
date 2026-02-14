package com.example.vroom.viewmodels;

import android.annotation.SuppressLint;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.map.DriverPositionDTO;
import com.example.vroom.DTOs.ride.requests.ComplaintRequestDTO;
import com.example.vroom.DTOs.ride.responses.RideUpdateResponseDTO;
import com.example.vroom.DTOs.ride.responses.UserActiveRideDTO;
import com.example.vroom.DTOs.route.responses.GetRouteResponseDTO;
import com.example.vroom.DTOs.route.responses.PointResponseDTO;
import com.example.vroom.enums.RideStatus;
import com.example.vroom.network.RetrofitClient;
import com.example.vroom.network.SocketProvider;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;
import com.google.gson.Gson;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideTrackingViewModel extends ViewModel {
    private final MutableLiveData<GetRouteResponseDTO> activeRoute = new MutableLiveData<>();
    public LiveData<GetRouteResponseDTO> getActiveRoute() { return activeRoute; }
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    private final MutableLiveData<RideUpdateResponseDTO> rideUpdate = new MutableLiveData<>();
    public LiveData<RideUpdateResponseDTO> getRideUpdate() { return rideUpdate; }
    private final MutableLiveData<String> message = new MutableLiveData<>();
    public LiveData<String> getMessage(){return message;}
    private final MutableLiveData<Boolean> isRideFinished = new MutableLiveData<>(false);
    public LiveData<Boolean> getIsRideFinished() { return isRideFinished; }
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final Gson gson = new Gson();
    private LocationCallback locationCallback;
    private boolean isTracking = false;

    public void loadRoute(Long rideID) {
        isLoading.setValue(true);
        RetrofitClient.getRideService().getRoute(rideID).enqueue(new Callback<GetRouteResponseDTO>() {
            @Override
            public void onResponse(Call<GetRouteResponseDTO> call, Response<GetRouteResponseDTO> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    activeRoute.setValue(response.body());
                } else {
                    error.setValue("Failed to fetch route");
                }
            }

            @Override
            public void onFailure(Call<GetRouteResponseDTO> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }

    public void subscribeToRideUpdates(Long rideID){
        disposables.add(SocketProvider.getInstance().getClient()
                .topic("/socket-publisher/ride-duration-update/" + rideID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    RideUpdateResponseDTO dto = gson.fromJson(message.getPayload(), RideUpdateResponseDTO.class);
                    rideUpdate.postValue(dto);
                    if (dto.getStatus() == RideStatus.FINISHED) {
                        isRideFinished.postValue(true);
                    }
                }, throwable -> {}));
    }

    public void unsubscribeFromRideUpdates(){
        this.disposables.clear();
    }

    @SuppressLint("MissingPermission")
    public void startTracking(FusedLocationProviderClient client, Long rideID) {
        if (isTracking) return;

        LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(2000)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                if (result != null && result.getLastLocation() != null && isTracking) {
                    sendCurrentLocation(rideID, result.getLastLocation().getLatitude(), result.getLastLocation().getLongitude());
                }
            }
        };

        client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
        isTracking = true;
    }

    public void stopTracking(FusedLocationProviderClient client) {
        isTracking = false;
        if (locationCallback != null && client != null) {
            client.removeLocationUpdates(locationCallback);
            locationCallback = null;
        }
        disposables.clear();
    }

    public void sendCurrentLocation(Long rideID, double lat, double lng) {
        if (!isTracking) return;

        PointResponseDTO location = new PointResponseDTO(lat, lng);
        String json = gson.toJson(location);

        disposables.add(SocketProvider.getInstance().getClient()
                .send("/socket-subscriber/ride-duration-update/" + rideID, json)
                .subscribe(() -> {}, throwable -> {
                    Log.e("SOCKET", "Failed to send location", throwable);
                }));
    }

    public void sendComplaint(Long rideID, String complaint){
        if (complaint == null || complaint.trim().isEmpty()) return;
        ComplaintRequestDTO requestDTO = new ComplaintRequestDTO(complaint);
        RetrofitClient.getRideService().sendComplaint(rideID, requestDTO).enqueue(new Callback<MessageResponseDTO>() {
            @Override
            public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null){
                    message.setValue(response.body().getMessage());
                }
            }
            @Override
            public void onFailure(Call<MessageResponseDTO> call, Throwable t) {}
        });
    }

    public void finishRide(Long rideID){
        RetrofitClient.getRideService().finishRide(rideID).enqueue(new Callback<MessageResponseDTO>() {
            @Override
            public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null){
                    message.setValue(response.body().getMessage());
                }
            }
            @Override
            public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
            }
        });
    }

}