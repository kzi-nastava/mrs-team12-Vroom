package com.example.vroom.viewmodels;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.admin.AdminUserDTO;
import com.example.vroom.DTOs.admin.BlockUserRequestDTO;
import com.example.vroom.data.local.StorageManager;
import com.example.vroom.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminViewModel extends ViewModel {
    private static final String TAG = "AdminViewModel";

    private final MutableLiveData<List<AdminUserDTO>> users = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public MutableLiveData<List<AdminUserDTO>> getUsers() {
        return users;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    public void loadUsers() {
        loading.postValue(true);

        String token = StorageManager.getData("jwt", "");
        Log.d(TAG, "Loading users with token: " + (token != null ? "EXISTS" : "NULL"));

        RetrofitClient.getAdminService().getAllUsers(token).enqueue(new Callback<List<AdminUserDTO>>() {
            @Override
            public void onResponse(Call<List<AdminUserDTO>> call, Response<List<AdminUserDTO>> response) {
                loading.postValue(false);

                try {

                    if (response.errorBody() != null) {
                        String errorBody = response.errorBody().string();
                        Log.d(TAG, "Error body: " + errorBody);
                    }

                    if (response.body() != null) {

                        String jsonBody = new com.google.gson.Gson().toJson(response.body());
                        Log.d(TAG, "Body as JSON string: " + jsonBody);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error logging response body", e);
                }

                if (response.isSuccessful() && response.body() != null) {
                    users.postValue(response.body());
                    Log.d(TAG, "Users loaded: " + response.body().size());
                } else {
                    errorMessage.postValue("Failed to load users: " + response.code());
                    Log.e(TAG, "Error loading users: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<AdminUserDTO>> call, Throwable t) {
                loading.postValue(false);
                errorMessage.postValue("Network error: " + t.getMessage());
                Log.e(TAG, "Network error: " + t.getMessage());
            }
        });
    }

    public void blockUser(Long userId, String reason) {
        loading.postValue(true);
        String token = StorageManager.getData("jwt", "");
        BlockUserRequestDTO request = new BlockUserRequestDTO(reason);

        RetrofitClient.getAdminService().blockUser(userId, request, token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loading.postValue(false);
                if (response.isSuccessful()) {
                    successMessage.postValue("User blocked successfully");
                    loadUsers();
                    Log.d(TAG, "User blocked: " + userId);
                } else {
                    errorMessage.postValue("Failed to block user: " + response.code());
                    Log.e(TAG, "Error blocking user: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                loading.postValue(false);
                errorMessage.postValue("Network error: " + t.getMessage());
                Log.e(TAG, "Network error: " + t.getMessage());
            }
        });
    }

    public void unblockUser(Long userId) {
        loading.postValue(true);
        String token = StorageManager.getData("jwt", "");

        RetrofitClient.getAdminService().unblockUser(userId, token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loading.postValue(false);
                if (response.isSuccessful()) {
                    successMessage.postValue("User unblocked successfully");
                    loadUsers();
                    Log.d(TAG, "User unblocked: " + userId);
                } else {
                    errorMessage.postValue("Failed to unblock user: " + response.code());
                    Log.e(TAG, "Error unblocking user: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                loading.postValue(false);
                errorMessage.postValue("Network error: " + t.getMessage());
                Log.e(TAG, "Network error: " + t.getMessage());
            }
        });
    }
}
