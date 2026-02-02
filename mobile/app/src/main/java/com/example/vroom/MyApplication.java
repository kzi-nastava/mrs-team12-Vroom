package com.example.vroom;

import android.app.Application;
import com.example.vroom.network.RetrofitClient;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitClient.init(this);
    }
}
