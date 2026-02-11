package com.example.vroom.viewmodels;

import android.content.Intent;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.auth.requests.ResetPasswordRequestDTO;
import com.example.vroom.activities.ForgotPasswordActivity;
import com.example.vroom.activities.LoginActivity;
import com.example.vroom.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordViewModel extends ViewModel {
    private final MutableLiveData<String> resetPasswordMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> resetPasswordStatus = new MutableLiveData<>();

    public MutableLiveData<String> getResetPasswordMessage() {
        return resetPasswordMessage;
    }

    public MutableLiveData<Boolean> getResetPasswordStatus() {
        return resetPasswordStatus;
    }

    public void resetPassword(String email, String code, String pass, String rePass){
        try{
            if (email.isEmpty() || code.isEmpty() || pass.isEmpty() || rePass.isEmpty())
                throw new Exception("Fields are missing");

            if(!pass.equals(rePass))
                throw new Exception("Password must match");


            ResetPasswordRequestDTO req = new ResetPasswordRequestDTO(email, code, pass, rePass);
            RetrofitClient.getAuthService().resetPassword(req).enqueue(new Callback<MessageResponseDTO>() {
                @Override
                public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                    if(response.isSuccessful() && response.body() != null){
                        resetPasswordMessage.postValue(response.body().getMessage());
                        resetPasswordStatus.postValue(true);
                    }else {
                        resetPasswordMessage.postValue(response.body().getMessage());
                        resetPasswordStatus.postValue(false);
                    }
                }

                @Override
                public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                    resetPasswordMessage.postValue("Network error: " + t.getMessage());
                    resetPasswordStatus.postValue(false);
                }
            });

        }catch(Exception e){
            resetPasswordMessage.postValue("Error: " + e.getMessage());
            resetPasswordStatus.postValue(false);
        }
    }
}
