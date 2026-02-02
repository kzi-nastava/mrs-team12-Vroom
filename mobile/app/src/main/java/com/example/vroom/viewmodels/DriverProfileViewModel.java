package com.example.vroom.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.driver.requests.DriverDTO;
import com.example.vroom.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverProfileViewModel extends ViewModel {

    private final MutableLiveData<DriverDTO> profile = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();

    public LiveData<DriverDTO> getProfile() {
        return profile;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadProfile() {
        RetrofitClient.getDriverProfileService()
                .getMyProfile()
                .enqueue(new Callback<DriverDTO>() {
                    @Override
                    public void onResponse(Call<DriverDTO> call, Response<DriverDTO> response) {
                        if (response.isSuccessful()) {
                            profile.postValue(response.body());
                        } else {
                            error.postValue("Failed to load driver profile: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<DriverDTO> call, Throwable t) {
                        error.postValue(t.getMessage());
                    }
                });
    }

    public void requestProfileUpdate(DriverDTO dto) {
        RetrofitClient.getDriverProfileService()
                .requestUpdate(dto)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            updateSuccess.postValue(true);
                        } else {
                            error.postValue("Update failed: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        error.postValue(t.getMessage());
                    }
                });
    }
    public LiveData<Boolean> getUpdateSuccess() {
        return updateSuccess;
    }
}
