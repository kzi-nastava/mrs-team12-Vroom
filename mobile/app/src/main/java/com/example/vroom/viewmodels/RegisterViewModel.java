package com.example.vroom.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.auth.requests.RegisterUserRequestDTO;
import com.example.vroom.activities.LoginActivity;
import com.example.vroom.activities.RegisterActivity;
import com.example.vroom.enums.Gender;
import com.example.vroom.network.RetrofitClient;
import com.example.vroom.utils.ImageUtils;
import com.example.vroom.utils.PasswordUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends ViewModel {
    private final MutableLiveData<String> registerMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registerStatus = new MutableLiveData<>();


    public MutableLiveData<String> getRegisterMessage() {
        return registerMessage;
    }

    public MutableLiveData<Boolean> getRegisterStatus() {
        return registerStatus;
    }

    private Gender getGender(String selectedGender){
        if(selectedGender.equalsIgnoreCase("male"))
            return Gender.MALE;
        else if (selectedGender.equalsIgnoreCase("FEMALE"))
            return Gender.FEMALE;
        else
            return Gender.OTHER;
    }
    public void register(
            String firstName,
            String lastName,
            String email,
            String phone,
            String country,
            String city,
            String street,
            String pass,
            String rePass,
            String selectedGender,
            byte[] photo
    ){
        try{
            if(
                    firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                            phone.isEmpty() || country.isEmpty() || city.isEmpty() ||
                            street.isEmpty() || pass.isEmpty() || rePass.isEmpty() ||
                            selectedGender.isEmpty()
            )
                throw new Exception("Fields cannot be empty");

            PasswordUtils.isPasswordValid(pass, rePass);
            String address = street + ", " + city + ", " + country;

            Gender gender = getGender(selectedGender);
            RegisterUserRequestDTO req = new RegisterUserRequestDTO(firstName, lastName, email, phone,
                    address, gender, photo, pass);

            RetrofitClient.getAuthService().registerUser(req).enqueue(new Callback<MessageResponseDTO>() {
                @Override
                public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                    if(response.isSuccessful() && response.body() != null){
                        registerMessage.postValue(response.body().getMessage());
                        registerStatus.postValue(true);
                    }else{
                        registerMessage.postValue(response.body().getMessage());
                        registerStatus.postValue(false);
                    }

                }

                @Override
                public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                    registerMessage.postValue("Network error: " + t.getMessage());
                    registerStatus.postValue(false);
                }
            });

        }catch(Exception e){
            registerMessage.postValue("Error: " + e.getMessage());
            registerStatus.postValue(false);
        }
    }
}
