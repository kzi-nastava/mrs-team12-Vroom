package com.example.vroom.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.vroom.R;

public class ProfileActivity extends BaseActivity {

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

    @Override
    public void onProfileButtonClicked(){
        Toast.makeText(this, "You're already Here !", Toast.LENGTH_SHORT).show();
    }
}
