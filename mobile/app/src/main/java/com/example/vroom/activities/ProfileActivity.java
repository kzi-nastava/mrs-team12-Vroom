package com.example.vroom.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.vroom.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isDriver = true;

        if (isDriver) {
            setContentView(R.layout.activity_driver_profile);
        } else {
            setContentView(R.layout.activity_user_profile);
        }
    }
}
