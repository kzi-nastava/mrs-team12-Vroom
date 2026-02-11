package com.example.vroom.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.panic.requests.PanicRequestDTO;
import com.example.vroom.DTOs.ride.responses.StoppedRideResponseDTO;
import com.example.vroom.network.RetrofitClient;

import java.time.LocalDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PanicViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<MessageResponseDTO> panicSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public MutableLiveData<MessageResponseDTO> getPanicSuccess() { return panicSuccess; }
    public MutableLiveData<String> getErrorMessage() { return errorMessage; }


    public void panicNotificationRequest(Long rideId){
        isLoading.setValue(true);

        PanicRequestDTO data = new PanicRequestDTO(rideId, LocalDateTime.now());

        RetrofitClient.getPanicNotificationService().createPanic(data).enqueue(new Callback<MessageResponseDTO>() {
            @Override
            public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                isLoading.setValue(false);
                if(response.isSuccessful()){
                    panicSuccess.setValue(response.body());
                }else{
                    errorMessage.setValue("Error " + response.code() + ": Failed to notify admins.");
                }
            }

            @Override
            public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }
}