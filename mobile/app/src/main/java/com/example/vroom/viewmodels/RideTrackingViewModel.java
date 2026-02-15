package com.example.vroom.viewmodels;

import android.annotation.SuppressLint;
import android.os.Looper;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.ride.requests.ComplaintRequestDTO;
import com.example.vroom.DTOs.ride.responses.RideUpdateResponseDTO;
import com.example.vroom.DTOs.route.responses.GetRouteResponseDTO;
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
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideTrackingViewModel extends ViewModel {
    private final MutableLiveData<GetRouteResponseDTO> activeRoute = new MutableLiveData<>();
    private final MutableLiveData<RideUpdateResponseDTO> rideUpdate = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRideFinished = new MutableLiveData<>(false);
    private final CompositeDisposable disposables = new CompositeDisposable();
    private Disposable rideDisposable;
    private Long currentRideId;
    private final Gson gson = new Gson();
    private LocationCallback locationCallback;
    private Disposable rideSubscription;
    private boolean isTracking = false;

    public LiveData<GetRouteResponseDTO> getActiveRoute() { return activeRoute; }
    public LiveData<RideUpdateResponseDTO> getRideUpdate() { return rideUpdate; }
    public LiveData<Boolean> getIsRideFinished() { return isRideFinished; }

    public void loadRoute(Long rideID) {
        RetrofitClient.getRideService().getRoute(rideID).enqueue(new Callback<GetRouteResponseDTO>() {
            @Override
            public void onResponse(Call<GetRouteResponseDTO> call, Response<GetRouteResponseDTO> response) {
                if (response.isSuccessful()) activeRoute.setValue(response.body());
            }
            @Override
            public void onFailure(Call<GetRouteResponseDTO> call, Throwable t) {}
        });
    }

    public void subscribeToRideUpdates(Long rideID) {
        if (rideDisposable != null && !rideDisposable.isDisposed() && rideID.equals(currentRideId)) return;
        if (rideDisposable != null) rideDisposable.dispose();
        currentRideId = rideID;
        rideDisposable = SocketProvider.getInstance().getClient()
                .topic("/socket-publisher/ride-duration-update/" + rideID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    RideUpdateResponseDTO update = gson.fromJson(message.getPayload(), RideUpdateResponseDTO.class);
                    rideUpdate.postValue(update);
                }, throwable -> Log.e("STOMP", "Error", throwable));
        disposables.add(rideDisposable);
    }

    public void unsubscribeFromRideUpdates() {
        if (rideDisposable != null) {
            rideDisposable.dispose();
            rideDisposable = null;
        }
        currentRideId = null;
    }

    @SuppressLint("MissingPermission")
    public void startTracking(FusedLocationProviderClient client, Long id) {
        if (isTracking) return;
        LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                if (result != null && result.getLastLocation() != null && isTracking) {
                    sendLocation(id, result.getLastLocation().getLatitude(), result.getLastLocation().getLongitude());
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
    }

    public void sendCurrentLocation(Long rideID, double lat, double lng) {
        if (!isTracking) return;
        PointResponseDTO location = new PointResponseDTO(lat, lng);
        disposables.add(SocketProvider.getInstance().getClient()
                .send("/socket-subscriber/ride-duration-update/" + rideID, gson.toJson(location))
                .subscribe(() -> {}, t -> {}));
    }

    public void finishRide(Long rideID) {
        RetrofitClient.getRideService().finishRide(rideID).enqueue(new Callback<MessageResponseDTO>() {
            @Override
            public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                if (response.isSuccessful()) isRideFinished.postValue(true);
            }
            @Override
            public void onFailure(Call<MessageResponseDTO> call, Throwable t) {}
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}