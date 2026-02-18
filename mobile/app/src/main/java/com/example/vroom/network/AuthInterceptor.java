package com.example.vroom.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.vroom.data.local.StorageManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private Context context;

    public AuthInterceptor(Context context){
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = StorageManager.getData("jwt", null);
        Request originalRequest = chain.request();
       // Log.d("AuthInterceptor", "=== REQUEST TO: " + originalRequest.url());
       // Log.d("AuthInterceptor", "Token exists: " + (token != null));
        originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder();

        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
            Log.d("AuthInterceptor", "✅ Token added to header");
        } else {
            Log.e("AuthInterceptor", "❌ NO TOKEN FOUND!");
        }
        Request newRequest = builder.build();

        return chain.proceed(newRequest);
    }
}
