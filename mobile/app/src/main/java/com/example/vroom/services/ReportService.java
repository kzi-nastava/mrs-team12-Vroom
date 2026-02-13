package com.example.vroom.services;


import com.example.vroom.DTOs.ride.requests.RideReportDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReportService {

    // For regular users (passengers)
    @GET("api/reports/me")
    Call<RideReportDTO> getMyReport(
            @Query("from") String from,
            @Query("to") String to
    );

    // For drivers
    @GET("api/reports/driver/me")
    Call<RideReportDTO> getMyDriverReport(
            @Query("from") String from,
            @Query("to") String to
    );

    // Admin - general report
    @GET("api/reports/admin")
    Call<RideReportDTO> getAdminReport(
            @Query("from") String from,
            @Query("to") String to
    );

    // Admin - specific user
    @GET("api/reports/admin/user/{userId}")
    Call<RideReportDTO> getAdminUserReport(
            @Path("userId") Long userId,
            @Query("from") String from,
            @Query("to") String to
    );

    // Admin - specific driver
    @GET("api/reports/admin/driver/{driverId}")
    Call<RideReportDTO> getAdminDriverReport(
            @Path("driverId") Long driverId,
            @Query("from") String from,
            @Query("to") String to
    );

    // Admin - all users
    @GET("api/reports/admin/users")
    Call<RideReportDTO> getAdminAllUsersReport(
            @Query("from") String from,
            @Query("to") String to
    );

    // Admin - all drivers
    @GET("api/reports/admin/drivers")
    Call<RideReportDTO> getAdminAllDriversReport(
            @Query("from") String from,
            @Query("to") String to
    );
}
