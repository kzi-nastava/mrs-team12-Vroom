package com.example.vroom.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.vroom.DTOs.auth.requests.ChangePasswordRequestDTO;
import com.example.vroom.DTOs.registeredUser.RegisteredUserDTO;
import com.example.vroom.DTOs.registeredUser.UpdateProfileRequestDTO;
import com.example.vroom.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<RegisteredUserDTO> profile = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> passwordChangeSuccess = new MutableLiveData<>();

    public LiveData<RegisteredUserDTO> getProfile() {
        return profile;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getUpdateSuccess() {
        return updateSuccess;
    }

    public LiveData<Boolean> getPasswordChangeSuccess() {
        return passwordChangeSuccess;
    }

    public void loadProfile() {
        RetrofitClient.getUserProfileService()
                .getMyProfile()
                .enqueue(new Callback<RegisteredUserDTO>() {
                    @Override
                    public void onResponse(
                            Call<RegisteredUserDTO> call,
                            Response<RegisteredUserDTO> response
                    ) {
                        if (response.isSuccessful()) {
                            profile.postValue(response.body());
                        } else {
                            error.postValue("Failed to load profile");
                        }
                    }

                    @Override
                    public void onFailure(Call<RegisteredUserDTO> call, Throwable t) {
                        error.postValue(t.getMessage());
                    }
                });
    }

    public void updateProfile(UpdateProfileRequestDTO dto) {
        RetrofitClient.getUserService()
                .updateProfile(dto)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            updateSuccess.postValue(true);
                            loadProfile();
                        } else {
                            error.postValue("Update failed: " + response.code());
                            updateSuccess.postValue(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        error.postValue(t.getMessage());
                        updateSuccess.postValue(false);
                    }
                });
    }

    public void changePassword(ChangePasswordRequestDTO dto) {
        RetrofitClient.getUserProfileService()
                .changePassword(dto)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            passwordChangeSuccess.postValue(true);
                        } else {
                            try {
                                String errorBody = response.errorBody() != null
                                        ? response.errorBody().string()
                                        : "Password change failed";
                                error.postValue(errorBody);
                            } catch (Exception e) {
                                error.postValue("Password change failed");
                            }
                            passwordChangeSuccess.postValue(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        error.postValue(t.getMessage());
                        passwordChangeSuccess.postValue(false);
                    }
                });
    }
}