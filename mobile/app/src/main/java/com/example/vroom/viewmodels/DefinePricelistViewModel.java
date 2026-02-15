package com.example.vroom.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.admin.PricelistDTO;
import com.example.vroom.network.RetrofitClient;
import com.example.vroom.services.AdminService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DefinePricelistViewModel extends ViewModel {
    private final MutableLiveData<PricelistDTO> currentPricelist = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateStatus = new MutableLiveData<>();

    public LiveData<PricelistDTO> getCurrentPricelist() {
        return currentPricelist;
    }

    public LiveData<Boolean> getUpdateStatus() {
        return updateStatus;
    }
    public void loadPricelist() {
        RetrofitClient.getAdminService().getActivePricelist().enqueue(new Callback<PricelistDTO>() {
            @Override
            public void onResponse(Call<PricelistDTO> call, Response<PricelistDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentPricelist.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<PricelistDTO> call, Throwable t) {
                currentPricelist.postValue(null);
            }
        });
    }

    public void savePricelist(double standard, double luxury, double minivan) {
        PricelistDTO dto = new PricelistDTO(standard, luxury, minivan);

        RetrofitClient.getAdminService().setPricelist(dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    updateStatus.postValue(true);
                    loadPricelist();
                } else {
                    updateStatus.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                updateStatus.postValue(false);
            }
        });
    }

    public void resetUpdateStatus() {
        updateStatus.setValue(null);
    }
}

