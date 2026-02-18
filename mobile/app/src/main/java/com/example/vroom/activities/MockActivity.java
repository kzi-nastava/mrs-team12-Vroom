package com.example.vroom.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);
        tv.setTextSize(24);
        tv.setText("Mock Screen");
        tv.setGravity(android.view.Gravity.CENTER);

        setContentView(tv);
    }
}