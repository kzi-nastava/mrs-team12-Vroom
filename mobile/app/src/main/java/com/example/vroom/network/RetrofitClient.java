package com.example.vroom.network;

import android.content.Context;

import com.example.vroom.services.AdminService;
import com.example.vroom.services.AuthService;
import com.example.vroom.services.ChatService;
import com.example.vroom.services.DriverProfileService;
import com.example.vroom.services.DriverService;
import com.example.vroom.services.GeoLocationService;
import com.example.vroom.services.PanicNotificationService;
import com.example.vroom.services.RegisteredUserService;
import com.example.vroom.services.ReportService;
import com.example.vroom.services.RideService;
import com.example.vroom.services.RouteService;
import com.example.vroom.services.UserProfileService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Context mContext;
    private static final String BASE_URL = "http://192.168.0.110:8080/";
    //private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static Retrofit retrofit = null;

    public static void init(Context context) {
        mContext = context;
    }

    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                        LocalDateTime.parse(json.getAsString()))
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                        LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))

                .create();
    }
    private static Retrofit getClient() {
        if (retrofit == null) {
            Gson gson = createGson();

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(mContext))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static ChatService getChatService() {return getClient().create(ChatService.class);}

    public static AuthService getAuthService(){
        return getClient().create(AuthService.class);
    }

    public static DriverService getDriverService() {
        return getClient().create(DriverService.class);
    }

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

    public static PanicNotificationService getPanicNotificationService(){ return getClient().create(PanicNotificationService.class); }

    public static AdminService getAdminService(){return getClient().create(AdminService.class);}
    public static RegisteredUserService getRegisteredUserService(){ return getClient().create(RegisteredUserService.class); }

    public static ReportService getReportService() {
        return getClient().create(ReportService.class);
    }
}
