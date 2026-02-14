package com.example.vroom.viewmodels;

import android.annotation.SuppressLint;
import android.os.Looper;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.vroom.DTOs.map.DriverPositionDTO;
import com.example.vroom.DTOs.map.MapRouteDTO;
import com.example.vroom.DTOs.map.OSRMEnvelope;
import com.example.vroom.DTOs.route.responses.PointResponseDTO;
import com.example.vroom.network.RetrofitClient;
import com.example.vroom.network.SocketProvider;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;
import com.google.gson.Gson;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<OSRMEnvelope.MapOSRMRoute> routeResult = new MutableLiveData<>();
    public LiveData<OSRMEnvelope.MapOSRMRoute> getRouteResult() { return routeResult; }
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public LiveData<String> getErrorMessage() { return errorMessage; }
    private final MutableLiveData<DriverPositionDTO> driverUpdate = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final Gson gson = new Gson();
    private LocationCallback locationCallback;
    private boolean isTracking = false;
    private boolean isRideTrackingActive = false;

    public LiveData<DriverPositionDTO> getDriverUpdate() { return driverUpdate; }

        public void setRideTrackingActive(boolean active){
            isRideTrackingActive = active;
            if (active){
                unsubscribeFromLocationUpdates();
            }
        }
        public void subscribeToLocationUpdates() {
        if (isRideTrackingActive) return;
        disposables.add(SocketProvider.getInstance().getClient()
                .topic("/socket-publisher/location-updates")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    DriverPositionDTO dto = gson.fromJson(message.getPayload(), DriverPositionDTO.class);
                    driverUpdate.postValue(dto);
                }, throwable -> {}));
    }

    public void unsubscribeFromLocationUpdates(){
        disposables.clear();
    }

    @SuppressLint("MissingPermission")
    public void startTracking(FusedLocationProviderClient client) {
        if (isTracking) return;

        LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(2000)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                if (result != null && result.getLastLocation() != null && isTracking) {
                    sendCurrentLocation(result.getLastLocation().getLatitude(), result.getLastLocation().getLongitude());
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

    public void sendCurrentLocation(double lat, double lng) {
        if (!isTracking) return; // Only check if tracking is enabled

        PointResponseDTO location = new PointResponseDTO(lat, lng);
        disposables.add(SocketProvider.getInstance().getClient()
                .send("/socket-subscriber/update-location", gson.toJson(location))
                .subscribe(() -> {}, t -> Log.e("SOCKET", "Global update failed", t)));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }

    private String getStringCoords(MapRouteDTO payload){
        List<String> coords = new ArrayList<>();

        if (payload.getStart() != null) {
            coords.add(payload.getStart().getLng() + "," + payload.getStart().getLat());
        }
        if (payload.getStops() != null) {
            for (PointResponseDTO stop : payload.getStops()) {
                coords.add(stop.getLng() + "," + stop.getLat());
            }
        }
        if (payload.getEnd() != null) {
            coords.add(payload.getEnd().getLng() + "," + payload.getEnd().getLat());
        }

        return android.text.TextUtils.join(";", coords);
    }

    // sends route payload
    public void getRouteCoordinates(MapRouteDTO payload) {
        // convert payload to string
        String coordString = getStringCoords(payload);

        if (coordString.isEmpty()) {
            errorMessage.setValue("Invalid route coordinates");
            return;
        }

        // request the osrm route for the payload
        RetrofitClient.getRouteService().getOSRMRoute(coordString).enqueue(new Callback<OSRMEnvelope>() {
            @Override
            public void onResponse(Call<OSRMEnvelope> call, Response<OSRMEnvelope> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OSRMEnvelope.MapOSRMRoute bestRoute = response.body().routes.get(0);
                    routeResult.postValue(bestRoute);
                } else {
                    errorMessage.postValue("Failed to get route: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<OSRMEnvelope> call, Throwable t) {
                errorMessage.postValue("Network error: " + t.getMessage());
            }
        });
    }

}