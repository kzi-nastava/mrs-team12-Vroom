package com.example.vroom.viewmodels;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.auth.requests.ForgotPasswordRequestDTO;
import com.example.vroom.DTOs.auth.requests.LoginRequestDTO;
import com.example.vroom.DTOs.auth.responses.LoginResponseDTO;
import com.example.vroom.activities.ForgotPasswordActivity;
import com.example.vroom.activities.LoginActivity;
import com.example.vroom.activities.MainActivity;
import com.example.vroom.data.local.StorageManager;
import com.example.vroom.network.RetrofitClient;
import com.example.vroom.network.SocketProvider;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {
    private final MutableLiveData<String> loginMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginStatus = new MutableLiveData<>();
    private final MutableLiveData<String> forgotPasswordMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> forgotPasswordStatus = new MutableLiveData<>();

    public MutableLiveData<String> getLoginMessage() {
        return loginMessage;
    }
    public MutableLiveData<Boolean> getLoginStatus() {
        return loginStatus;
    }

    public MutableLiveData<String> getForgotPasswordMessage() {
        return forgotPasswordMessage;
    }

    public MutableLiveData<Boolean> getForgotPasswordStatus() {
        return forgotPasswordStatus;
    }

    public void forgotPassword(String email){
        try{
            if (email.isEmpty())
                throw new Exception("Email is missing");

            ForgotPasswordRequestDTO req = new ForgotPasswordRequestDTO(email);
            RetrofitClient.getAuthService().forgotPassword(req).enqueue(new Callback<MessageResponseDTO>() {
                @Override
                public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                    if(response.isSuccessful() && response.body() != null){
                        forgotPasswordMessage.postValue(response.body().getMessage());
                        forgotPasswordStatus.postValue(true);
                    }else {
                        forgotPasswordMessage.postValue(response.body().getMessage());
                        forgotPasswordStatus.postValue(false);
                    }
                }
                @Override
                public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                    forgotPasswordMessage.postValue("Network error: " + t.getMessage());
                    forgotPasswordStatus.postValue(false);
                }
            });
        }catch (Exception e){
            forgotPasswordMessage.postValue("Error: " + e.getMessage());
            forgotPasswordStatus.postValue(false);
        }
    }

    public void login(String email, String password){
        try{
            if (email.isEmpty())
                throw new Exception("Email is missing");

            if (password.isEmpty())
                throw new Exception("Password is missing");

            LoginRequestDTO req = new LoginRequestDTO(email, password);

            RetrofitClient.getAuthService().login(req).enqueue(new Callback<LoginResponseDTO>() {
                @Override
                public void onResponse(Call<LoginResponseDTO> call, Response<LoginResponseDTO> response) {
                    if(response.isSuccessful() && response.body() != null){
                        LoginResponseDTO body = response.body();

                        StorageManager.saveData("user_type", response.body().getType());
                        StorageManager.saveData("jwt", response.body().getToken());
                        Long expiresTime = body.getExpires();
                        if (expiresTime == null || expiresTime <= 0) {
                            expiresTime = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000);
                        } else {
                        }

                        StorageManager.saveLong("expires", expiresTime);
                        SocketProvider.getInstance().init();
                        loginMessage.postValue("Login successful");
                        loginStatus.postValue(true);
                    }else{
                        loginMessage.postValue("Server error: " + response.code());
                        loginStatus.postValue(false);
                    }
                }

                @Override
                public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                    loginMessage.postValue("Network error: " + t.getMessage());
                    loginStatus.postValue(false);
                }
            });

        }catch(Exception e){
            loginMessage.postValue("Error: " + e.getMessage());
            loginStatus.postValue(false);
        }
    }
}
