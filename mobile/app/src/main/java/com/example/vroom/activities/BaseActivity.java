package com.example.vroom.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.vroom.DTOs.map.MapRouteDTO;
import com.example.vroom.DTOs.ride.responses.UserActiveRideDTO;
import com.example.vroom.DTOs.route.responses.PointResponseDTO;
import com.example.vroom.R;
import com.example.vroom.data.local.StorageManager;
import com.example.vroom.fragments.PanicFeedFragment;
import com.example.vroom.fragments.RideHistoryFragment;
import com.example.vroom.fragments.RouteEstimationFragment;
import com.example.vroom.fragments.UserActiveRideFragment;
import com.example.vroom.fragments.UserRideHistoryFragment;
import com.example.vroom.network.SocketProvider;
import com.example.vroom.viewmodels.NavigationViewModel;
import com.google.android.material.navigation.NavigationView;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Toolbar toolbar;
    ImageButton logoButton;
    ImageButton profileButton;
    private FrameLayout contentFrame;
    private DrawerLayout drawer;
    private NavigationViewModel viewModel;
    private RouteEstimationFragment routeEstimationFragment;

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

        viewModel = new ViewModelProvider(this).get(NavigationViewModel.class);
        observeViewModel();

        initToggleStatus(navigationView);
    }

    private void observeViewModel() {
        viewModel.getDriverAvailable().observe(this, isAvailable -> {
            NavigationView navigationView = findViewById(R.id.nav_view);
            MenuItem switchItem = navigationView.getMenu()
                    .findItem(R.id.nav_status_switch);
            Switch statusSwitch = (Switch) switchItem.getActionView();

            statusSwitch.setChecked(isAvailable);
            switchItem.setTitle(isAvailable
                    ? "Status: Active"
                    : "Status: Inactive");
        });

        viewModel.getToastMessage().observe(this, message -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });

        viewModel.getLogoutSuccess().observe(this, success -> {
            if (success) {
                finalizeLogout();
            }
        });
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
    private void initToggleStatus(NavigationView navigationView){
        MenuItem switchItem = navigationView.getMenu().findItem(R.id.nav_status_switch);
        if (switchItem != null) {
            Switch statusSwitch = (Switch) switchItem.getActionView();
            if (statusSwitch != null) {
                statusSwitch.setChecked(true);
                switchItem.setTitle("Status: Active");

                statusSwitch.setOnCheckedChangeListener(
                        (buttonView, isChecked) -> viewModel.changeDriverStatus(isChecked)
                );
            }
        }
    }
    private void updateMenuVisibility() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView == null) return;

        android.view.Menu menu = navigationView.getMenu();

        StorageManager.getSharedPreferences(this);
        String token = StorageManager.getData("jwt", null);
        String userType = StorageManager.getData("user_type", null);
        boolean isLoggedIn = (token != null && !token.isEmpty());

        menu.findItem(R.id.nav_logout).setVisible(isLoggedIn);
        menu.findItem(R.id.driver_ride_history_item).setVisible(isLoggedIn);
        menu.findItem(R.id.nav_status_switch).setVisible(isLoggedIn && userType.equals("DRIVER"));

        menu.findItem(R.id.nav_panic_feed).setVisible(isLoggedIn && userType.equals("ADMIN"));
        menu.findItem(R.id.ride_history).setVisible(isLoggedIn &&
                (userType.equals("ADMIN") || userType.equals("REGISTERED_USER")));
        menu.findItem(R.id.user_active_rides).setVisible(isLoggedIn && userType.equals("REGISTERED_USER"));
        menu.findItem(R.id.login_navbar_item).setVisible(!isLoggedIn);
        menu.findItem(R.id.register_navbar_item).setVisible(!isLoggedIn);
        menu.findItem(R.id.nav_route_estimation).setVisible(!isLoggedIn);
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onLogoButtonClicked(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onProfileButtonClicked(){
        StorageManager.getSharedPreferences(this);
        String userType = StorageManager.getData("user_type", null);

        Intent intent;
        if ("DRIVER".equals(userType)) {
            intent = new Intent(this, DriverProfileActivity.class);
        } else {
            intent = new Intent(this, ProfileActivity.class);
        }

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
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new RideHistoryFragment())
                    .addToBackStack(null)
                    .commit();
        }else if (id == R.id.nav_logout && StorageManager.getData("jwt", null) != null){
            viewModel.logout();
        }else if(id == R.id.nav_route_estimation){
            if(routeEstimationFragment == null)
                routeEstimationFragment = RouteEstimationFragment.newInstance();

            routeEstimationFragment.show(getSupportFragmentManager(), "RouteEstimationBottomSheet");
        }else if(id == R.id.nav_panic_feed){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new PanicFeedFragment())
                    .addToBackStack(null)
                    .commit();
        }else if(id == R.id.ride_history){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new UserRideHistoryFragment())
                    .addToBackStack(null)
                    .commit();
        }else if (id == R.id.user_active_rides){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new UserActiveRideFragment())
                    .addToBackStack(null)
                    .commit();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void finalizeLogout(){
        StorageManager.getSharedPreferences(this).edit().clear().apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}