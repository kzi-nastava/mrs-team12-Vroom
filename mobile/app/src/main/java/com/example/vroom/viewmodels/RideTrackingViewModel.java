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
import com.example.vroom.enums.RideStatus;
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
    private final MutableLiveData<Long> rideID = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final Gson gson = new Gson();
    private LocationCallback locationCallback;
    private Disposable rideSubscription;
    private boolean isTracking = false;

    public LiveData<GetRouteResponseDTO> getActiveRoute() { return activeRoute; }
    public LiveData<RideUpdateResponseDTO> getRideUpdate() { return rideUpdate; }
    public LiveData<Boolean> getIsRideFinished() { return isRideFinished; }
    public LiveData<Long> getRideID() { return rideID; }

    public void loadRoute(Long id) {
        this.rideID.setValue(id);
        RetrofitClient.getRideService().getRoute(id).enqueue(new Callback<GetRouteResponseDTO>() {
            @Override
            public void onResponse(Call<GetRouteResponseDTO> call, Response<GetRouteResponseDTO> response) {
                if (response.isSuccessful()) activeRoute.setValue(response.body());
            }
            @Override
            public void onFailure(Call<GetRouteResponseDTO> call, Throwable t) {}
        });
    }

    public void subscribeToRideUpdates(Long id) {
        if (rideSubscription != null && !rideSubscription.isDisposed()) return;
        rideSubscription = SocketProvider.getInstance().getClient()
                .topic("/socket-publisher/ride-duration-update/" + id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    RideUpdateResponseDTO dto = gson.fromJson(message.getPayload(), RideUpdateResponseDTO.class);
                    rideUpdate.postValue(dto);
                    if (dto.getStatus() == RideStatus.FINISHED) isRideFinished.postValue(true);
                }, t -> Log.e("SOCKET", "Subscription failed", t));
        disposables.add(rideSubscription);
    }

    public void unsubscribeFromRideUpdates() {
        if (rideSubscription != null && !rideSubscription.isDisposed()) {
            rideSubscription.dispose();
            rideSubscription = null;
        }
    }

    @SuppressLint("MissingPermission")
    public void startTracking(FusedLocationProviderClient client, Long id) {
        if (isTracking) return;
        LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(2000)
                .build();
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

    private void sendLocation(Long id, double lat, double lng) {
        String json = gson.toJson(new PointResponseDTO(lat, lng));
        disposables.add(SocketProvider.getInstance().getClient()
                .send("/socket-subscriber/ride-duration-update/" + id, json)
                .subscribe(() -> {}, t -> Log.e("SOCKET", "Send failed", t)));
    }

    public void finishRide(Long id) {
        RetrofitClient.getRideService().finishRide(id).enqueue(new Callback<MessageResponseDTO>() {
            @Override
            public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {}
            @Override
            public void onFailure(Call<MessageResponseDTO> call, Throwable t) {}
        });
    }

    public void sendComplaint(Long id, String text) {
        RetrofitClient.getRideService().sendComplaint(id, new ComplaintRequestDTO(text)).enqueue(new Callback<MessageResponseDTO>() {
            @Override
            public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {}
            @Override
            public void onFailure(Call<MessageResponseDTO> call, Throwable t) {}
        });
    }

    public void resetState() {
        isRideFinished.setValue(false);
        rideUpdate.setValue(null);
        activeRoute.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        unsubscribeFromRideUpdates();
        disposables.clear();
    }
}