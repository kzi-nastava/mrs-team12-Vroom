package com.example.vroom.network;

import android.content.Context;

import com.example.vroom.services.AuthService;
import com.example.vroom.services.DriverProfileService;
import com.example.vroom.services.DriverService;
import com.example.vroom.services.GeoLocationService;
import com.example.vroom.services.RideService;
import com.example.vroom.services.RouteService;
import com.example.vroom.services.UserProfileService;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Context mContext;
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static Retrofit retrofit = null;

    public static void init(Context context) {
        mContext = context;
    }
    private static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(mContext))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static AuthService getAuthService(){
        return getClient().create(AuthService.class);
    }
    public static DriverService getDriverService() {return getClient().create(DriverService.class);}
    public static UserProfileService getUserProfileService() {
        return getClient().create(UserProfileService.class);
    }
    public static RideService getRideService() {
        return getClient().create(RideService.class);
    }

    public static UserProfileService getUserService() {
        return getClient().create(UserProfileService.class);
    }
    public static DriverProfileService getDriverProfileService() {
        return getClient().create(DriverProfileService.class);
    }

    public static RouteService getRouteService(){ return getClient().create(RouteService.class); }
    public static GeoLocationService getGeoLocationService(){ return getClient().create(GeoLocationService.class); }
}
