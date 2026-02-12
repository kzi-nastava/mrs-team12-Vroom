package com.example.vroom.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.vroom.data.local.StorageManager;
import android.util.Log;
import com.example.vroom.network.RetrofitClient;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StorageManager.getSharedPreferences(this);
        RetrofitClient.init(getApplicationContext());

        String token = StorageManager.getData("jwt", null);
        long expires = StorageManager.getLong("expires", -1L);
        String role = StorageManager.getData("user_type", null);

        Log.d(TAG, "Token: " + (token != null ? "EXISTS" : "NULL"));
        Log.d(TAG, "Expires: " + expires);
        Log.d(TAG, "Current time: " + System.currentTimeMillis());
        Log.d(TAG, "Role: " + role);
        Log.d(TAG, "Token valid: " + (token != null && System.currentTimeMillis() < expires));

        navigateToAppropriateActivity();
    }

    private void navigateToAppropriateActivity() {
        String token = StorageManager.getData("jwt", null);
        long expires = StorageManager.getLong("expires", -1L);

        Intent intent;

        if (token != null && System.currentTimeMillis() < expires) {
            String role = StorageManager.getData("user_type", null);

            Log.d(TAG, "Valid token found. Navigating based on role: " + role);

            if ("ADMIN".equals(role)) {
                intent = new Intent(this, AdminActivity.class);
                Log.d(TAG, "Opening AdminActivity");
            } else {
                intent = new Intent(this, MainActivity.class);
                Log.d(TAG, "Opening MainActivity");
            }
        } else {
            Log.d(TAG, "No valid token. Opening LoginActivity");
            intent = new Intent(this, MainActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}