package com.example.vroom.viewmodels;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.enums.Gender;
import com.example.vroom.network.RetrofitClient;
import com.example.vroom.utils.PasswordUtils;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    private MultipartBody.Part getPhoto(byte[] photo){
        if(photo == null || photo.length == 0) return null;

        RequestBody photoBody = RequestBody.create(
                MediaType.parse("image/*"),
                photo
        );

        return MultipartBody.Part.createFormData(
                "profilePhoto",
                "profile.jpg",
                photoBody
        );

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

            RequestBody fNameBody = RequestBody.create(MediaType.parse("text/plain"), firstName);
            RequestBody lNameBody = RequestBody.create(MediaType.parse("text/plain"), lastName);
            RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email);
            RequestBody phoneBody = RequestBody.create(MediaType.parse("text/plain"), phone);
            RequestBody addressBody = RequestBody.create(MediaType.parse("text/plain"), address);
            RequestBody passBody = RequestBody.create(MediaType.parse("text/plain"), pass);
            RequestBody genderBody = RequestBody.create(MediaType.parse("text/plain"), getGender(selectedGender).name());

            MultipartBody.Part photoPart = getPhoto(photo);

            RetrofitClient.getAuthService().registerUser(
                    fNameBody, lNameBody, emailBody, phoneBody,
                    addressBody, genderBody, passBody, photoPart
            ).enqueue(new Callback<MessageResponseDTO>() {
                @Override
                public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                    if(response.isSuccessful() && response.body() != null){
                        registerMessage.postValue(response.body().getMessage());
                        registerStatus.postValue(true);
                    }else{
                        String errorMsg = "Something went wrong";
                        try {
                            if (response.errorBody() != null) {
                                errorMsg = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        registerMessage.postValue(errorMsg);
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
