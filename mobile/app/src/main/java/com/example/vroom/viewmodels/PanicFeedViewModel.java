package com.example.vroom.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.panic.responses.PanicNotificationResponseDTO;
import com.example.vroom.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PanicFeedViewModel extends ViewModel{
    private final MutableLiveData<List<PanicNotificationResponseDTO>> panicAlerts = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    public LiveData<List<PanicNotificationResponseDTO>> getPanicAlerts() { return panicAlerts; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<String> getSuccessMessage() { return successMessage; }

    public void loadAlerts() {
        isLoading.setValue(true);
        RetrofitClient.getPanicNotificationService().getPanicNotifications(true).enqueue(new Callback<List<PanicNotificationResponseDTO>>() {
            @Override
            public void onResponse(Call<List<PanicNotificationResponseDTO>> call, Response<List<PanicNotificationResponseDTO>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    panicAlerts.setValue(response.body());
                } else {
                    errorMessage.setValue("There has been an error with loading alerts");
                }
            }

            @Override
            public void onFailure(Call<List<PanicNotificationResponseDTO>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }

    public void resolvePanic(Long panicId, int position) {
        RetrofitClient.getPanicNotificationService().resolvePanic(panicId).enqueue(new Callback<MessageResponseDTO>() {
            @Override
            public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                if (response.isSuccessful()) {
                    List<PanicNotificationResponseDTO> currentList = new ArrayList<>(panicAlerts.getValue());
                    if (position < currentList.size()) {
                        currentList.remove(position);
                        panicAlerts.setValue(currentList);
                        successMessage.setValue("Resolved issue");
                    }
                } else {
                    errorMessage.setValue("Could not resolve panic alert");
                }
            }

            @Override
            public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }

}
