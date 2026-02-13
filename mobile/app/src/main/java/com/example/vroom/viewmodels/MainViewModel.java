package com.example.vroom.viewmodels;

import android.media.MediaRouter2;
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
import com.google.gson.Gson;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
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

    public LiveData<DriverPositionDTO> getDriverUpdate() {
        return driverUpdate;
    }

    public void subscribeToLocationUpdates() {
        disposables.add(SocketProvider.getInstance().getClient()
                .topic("/socket-publisher/location-updates")
                .subscribeOn(Schedulers.io())
                .subscribe(message -> {
                    DriverPositionDTO dto = gson.fromJson(message.getPayload(), DriverPositionDTO.class);
                    driverUpdate.postValue(dto);
                    Log.println(1, "MAIN VIEW MODEL", "EVERYTHINGS FINE");
                }, throwable -> {
                    Log.println(1, "MAIN VIEW MODEL", "ERROR WHILE CONNECTING DRIVER LOCATION WEB SOCKET ");
                }));
    }

    public void sendCurrentLocation(double lat, double lng) {
        PointResponseDTO location = new PointResponseDTO(lat, lng);
        String json = gson.toJson(location);

        SocketProvider.getInstance().getClient()
                .send("/socket-subscriber/update-location", json)
                .subscribe();
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
