package com.example.vroom.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.ride.requests.RideReportDTO;
import com.example.vroom.network.RetrofitClient;
import com.example.vroom.services.ReportService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideStatisticsViewModel extends ViewModel {

    private final MutableLiveData<RideReportDTO> report = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private final ReportService reportService = RetrofitClient.getReportService();

    public LiveData<RideReportDTO> getReport() {
        return report;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    // For regular users
    public void fetchMyReport(String from, String to) {
        isLoading.setValue(true);

        reportService.getMyReport(from, to).enqueue(new Callback<RideReportDTO>() {
            @Override
            public void onResponse(Call<RideReportDTO> call, Response<RideReportDTO> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    report.setValue(response.body());
                } else {
                    error.setValue("Failed to load report");
                }
            }

            @Override
            public void onFailure(Call<RideReportDTO> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e("REPORT_ERROR", "Failure", t);
            }
        });
    }

    // For drivers
    public void fetchMyDriverReport(String from, String to) {
        isLoading.setValue(true);

        reportService.getMyDriverReport(from, to).enqueue(new Callback<RideReportDTO>() {
            @Override
            public void onResponse(Call<RideReportDTO> call, Response<RideReportDTO> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    report.setValue(response.body());
                } else {
                    error.setValue("Failed to load report");
                }
            }

            @Override
            public void onFailure(Call<RideReportDTO> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e("REPORT_ERROR", "Failure", t);
            }
        });
    }

    // Admin - general
    public void fetchAdminReport(String from, String to) {
        isLoading.setValue(true);

        reportService.getAdminReport(from, to).enqueue(new Callback<RideReportDTO>() {
            @Override
            public void onResponse(Call<RideReportDTO> call, Response<RideReportDTO> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    report.setValue(response.body());
                } else {
                    error.setValue("Failed to load report");
                }
            }

            @Override
            public void onFailure(Call<RideReportDTO> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e("REPORT_ERROR", "Failure", t);
            }
        });
    }

    // Admin - specific user
    public void fetchAdminUserReport(Long userId, String from, String to) {
        isLoading.setValue(true);

        reportService.getAdminUserReport(userId, from, to).enqueue(new Callback<RideReportDTO>() {
            @Override
            public void onResponse(Call<RideReportDTO> call, Response<RideReportDTO> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    report.setValue(response.body());
                } else {
                    error.setValue("Failed to load report");
                }
            }

            @Override
            public void onFailure(Call<RideReportDTO> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e("REPORT_ERROR", "Failure", t);
            }
        });
    }

    // Admin - specific driver
    public void fetchAdminDriverReport(Long driverId, String from, String to) {
        isLoading.setValue(true);

        reportService.getAdminDriverReport(driverId, from, to).enqueue(new Callback<RideReportDTO>() {
            @Override
            public void onResponse(Call<RideReportDTO> call, Response<RideReportDTO> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    report.setValue(response.body());
                } else {
                    error.setValue("Failed to load report");
                }
            }

            @Override
            public void onFailure(Call<RideReportDTO> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e("REPORT_ERROR", "Failure", t);
            }
        });
    }

    // Admin - all users
    public void fetchAdminAllUsersReport(String from, String to) {
        isLoading.setValue(true);

        reportService.getAdminAllUsersReport(from, to).enqueue(new Callback<RideReportDTO>() {
            @Override
            public void onResponse(Call<RideReportDTO> call, Response<RideReportDTO> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    report.setValue(response.body());
                } else {
                    error.setValue("Failed to load report");
                }
            }

            @Override
            public void onFailure(Call<RideReportDTO> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e("REPORT_ERROR", "Failure", t);
            }
        });
    }

    // Admin - all drivers
    public void fetchAdminAllDriversReport(String from, String to) {
        isLoading.setValue(true);

        reportService.getAdminAllDriversReport(from, to).enqueue(new Callback<RideReportDTO>() {
            @Override
            public void onResponse(Call<RideReportDTO> call, Response<RideReportDTO> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    report.setValue(response.body());
                } else {
                    error.setValue("Failed to load report");
                }
            }

            @Override
            public void onFailure(Call<RideReportDTO> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e("REPORT_ERROR", "Failure", t);
            }
        });
    }
}