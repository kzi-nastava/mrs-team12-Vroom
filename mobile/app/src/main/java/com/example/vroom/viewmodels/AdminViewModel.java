package com.example.vroom.viewmodels;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.admin.AdminUserDTO;
import com.example.vroom.DTOs.admin.BlockUserRequestDTO;
import com.example.vroom.DTOs.admin.DriverUpdateDTO;
import com.example.vroom.DTOs.admin.DriverUpdateRequestAdminDTO;
import com.example.vroom.DTOs.admin.RejectRequestDTO;
import com.example.vroom.DTOs.driver.requests.DriverDTO;
import com.example.vroom.data.local.StorageManager;
import com.example.vroom.enums.DriverStatus;
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

    private MutableLiveData<List<DriverUpdateRequestAdminDTO>> profileRequests = new MutableLiveData<>();

    public LiveData<List<DriverUpdateRequestAdminDTO>> getProfileRequests() {
        return profileRequests;
    }

    public void loadProfileRequests() {
        loading.postValue(true);
        String token = "Bearer " + StorageManager.getData("jwt", null);

        RetrofitClient.getAdminService().getPendingRequests(token).enqueue(new Callback<List<DriverUpdateRequestAdminDTO>>() {
            @Override
            public void onResponse(Call<List<DriverUpdateRequestAdminDTO>> call, Response<List<DriverUpdateRequestAdminDTO>> response) {
                loading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    profileRequests.postValue(response.body());
                    Log.d(TAG, "Loaded " + response.body().size() + " pending requests");
                } else {
                    errorMessage.postValue("Failed to load requests: " + response.code());
                    Log.e(TAG, "Error loading requests: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<DriverUpdateRequestAdminDTO>> call, Throwable t) {
                loading.postValue(false);
                errorMessage.postValue("Network error: " + t.getMessage());
                Log.e(TAG, "Network error: " + t.getMessage());
            }
        });
    }

    public void approveRequest(Long requestId) {
        loading.postValue(true);
        String token = "Bearer " + StorageManager.getData("jwt", null);

        RetrofitClient.getAdminService().approveRequest(requestId, token)
                .enqueue(new Callback<DriverUpdateDTO>() {
                    @Override
                    public void onResponse(Call<DriverUpdateDTO> call, Response<DriverUpdateDTO> response) {
                        loading.postValue(false);
                        if (response.isSuccessful() && response.body() != null) {
                            DriverUpdateDTO updatedDriver = response.body();
                            successMessage.postValue("Request approved successfully");

                            List<AdminUserDTO> currentUsers = users.getValue();
                            if (currentUsers != null) {
                                for (AdminUserDTO user : currentUsers) {
                                    if (user.getId().equals(updatedDriver.getId())) {
                                        user.setBlocked(updatedDriver.getStatus() == DriverStatus.BLOCKED);
                                        user.setFirstName(updatedDriver.getFirstName());
                                        user.setLastName(updatedDriver.getLastName());
                                        user.setEmail(updatedDriver.getEmail());
                                        user.setAddress(updatedDriver.getAddress());
                                        user.setTelephoneNumber(updatedDriver.getPhoneNumber());
                                        break;
                                    }
                                }
                                users.postValue(currentUsers);
                            }

                            loadProfileRequests();
                            Log.d(TAG, "Request approved: " + requestId);
                        } else {
                            errorMessage.postValue("Failed to approve: " + response.code());
                            Log.e(TAG, "Error approving request: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<DriverUpdateDTO> call, Throwable t) {
                        loading.postValue(false);
                        errorMessage.postValue("Network error: " + t.getMessage());
                        Log.e(TAG, "Network error: " + t.getMessage());
                    }
                });
    }

    public void rejectRequest(Long requestId, String comment) {
        loading.postValue(true);
        String token = "Bearer " + StorageManager.getData("jwt", null);
        RejectRequestDTO dto = new RejectRequestDTO(comment);

        RetrofitClient.getAdminService().rejectRequest(requestId, dto, token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loading.postValue(false);
                if (response.isSuccessful()) {
                    successMessage.postValue("Request rejected successfully");
                    loadProfileRequests();
                    Log.d(TAG, "Request rejected: " + requestId);
                } else {
                    errorMessage.postValue("Failed to reject: " + response.code());
                    Log.e(TAG, "Error rejecting request: " + response.code());
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
