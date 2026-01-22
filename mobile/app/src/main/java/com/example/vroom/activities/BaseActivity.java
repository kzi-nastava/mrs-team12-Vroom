package com.example.vroom.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.auth.requests.LogoutRequestDTO;
import com.example.vroom.R;
import com.example.vroom.data.local.StorageManager;
import com.example.vroom.network.RetrofitClient;
import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Toolbar toolbar;
    ImageButton logoButton;
    ImageButton profileButton;
    private FrameLayout contentFrame;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        super.setContentView(R.layout.activity_base);
        contentFrame = findViewById(R.id.content_frame);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        logoButton = findViewById(R.id.logo);
        logoButton.setOnClickListener(v -> {
            onLogoButtonClicked();
        });

        profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(v -> {
            onProfileButtonClicked();
        });

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_button);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMenuVisibility();
    }

    @Override
    public void setContentView(int layoutResID) {
        // Inflate child layout into content frame
        if (contentFrame != null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(layoutResID, contentFrame, false);
            contentFrame.removeAllViews();
            contentFrame.addView(view);
        } else {
            super.setContentView(layoutResID);
        }
    }

    private void updateMenuVisibility() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView == null) return;

        android.view.Menu menu = navigationView.getMenu();

        StorageManager.getSharedPreferences(this);
        String token = StorageManager.getData("jwt", null);
        boolean isLoggedIn = (token != null && !token.isEmpty());

        menu.findItem(R.id.nav_logout).setVisible(isLoggedIn);
        menu.findItem(R.id.driver_ride_history_item).setVisible(isLoggedIn);

        menu.findItem(R.id.login_navbar_item).setVisible(!isLoggedIn);
        menu.findItem(R.id.register_navbar_item).setVisible(!isLoggedIn);
    }

    public void onLogoButtonClicked(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onProfileButtonClicked(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.login_navbar_item){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }else if (id == R.id.register_navbar_item){
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }else if (id == R.id.driver_ride_history_item){
            Intent intent = new Intent(this, DriverRideHistoryActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_logout && StorageManager.getData("jwt", null) != null){
            executeLogoutRequest();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void executeLogoutRequest(){
        LogoutRequestDTO req = new LogoutRequestDTO(StorageManager.getLong("user_id", -1L), StorageManager.getData("user_type", null));
        RetrofitClient.getAuthService().logout(req).enqueue(new Callback<MessageResponseDTO>() {
            @Override
            public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                finalizeLogout();
            }

            @Override
            public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                finalizeLogout();
            }
        });
    }

    private void finalizeLogout(){
        StorageManager.getSharedPreferences(this);
        StorageManager.clearAll();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}