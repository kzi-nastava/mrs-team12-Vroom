package com.example.vroom.viewmodels;

import android.content.Context;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.auth.requests.LogoutRequestDTO;
import com.example.vroom.DTOs.driver.requests.DriverChangeStatusRequestDTO;
import com.example.vroom.R;
import com.example.vroom.activities.BaseActivity;
import com.example.vroom.data.local.StorageManager;
import com.example.vroom.enums.DriverStatus;
import com.example.vroom.network.RetrofitClient;
import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavigationViewModel extends ViewModel {
    private final MutableLiveData<Boolean> driverAvailable = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> logoutSuccess = new MutableLiveData<>();

    public LiveData<Boolean> getDriverAvailable() {
        return driverAvailable;
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public LiveData<Boolean> getLogoutSuccess() {
        return logoutSuccess;
    }

    public void changeDriverStatus(boolean isChecked) {
        long driverId = StorageManager.getLong("user_id", -1L);

        DriverStatus status = isChecked
                ? DriverStatus.AVAILABLE
                : DriverStatus.UNAVAILABLE;

        DriverChangeStatusRequestDTO req =
                new DriverChangeStatusRequestDTO(status);

        RetrofitClient.getDriverService()
                .changeDriverStatus(driverId, req)
                .enqueue(new Callback<MessageResponseDTO>() {
                    @Override
                    public void onResponse(Call<MessageResponseDTO> call,
                                           Response<MessageResponseDTO> response) {
                        if (response.isSuccessful()) {
                            driverAvailable.postValue(isChecked);
                            toastMessage.postValue(
                                    isChecked ? "You are available now" : "You are unavailable now"
                            );
                        } else {
                            driverAvailable.postValue(!isChecked);
                            toastMessage.postValue("Server error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                        driverAvailable.postValue(!isChecked);
                        toastMessage.postValue("Network error");
                    }
                });
    }

    public void logout() {
        LogoutRequestDTO req = new LogoutRequestDTO(
                StorageManager.getLong("user_id", -1L),
                StorageManager.getData("user_type", null)
        );

        RetrofitClient.getAuthService().logout(req).enqueue(new Callback<MessageResponseDTO>() {
            @Override
            public void onResponse(Call<MessageResponseDTO> call,
                                   Response<MessageResponseDTO> response) {
                logoutSuccess.postValue(true);
            }

            @Override
            public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                logoutSuccess.postValue(true);
            }
        });
    }

}
