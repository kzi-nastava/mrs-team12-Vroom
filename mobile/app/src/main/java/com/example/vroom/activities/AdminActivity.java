package com.example.vroom.activities;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.vroom.R;
import com.example.vroom.data.local.StorageManager;
import com.example.vroom.fragments.BlockUserFragment;
import com.example.vroom.fragments.DefinePricelistFragment;
import com.example.vroom.fragments.PanicFeedFragment;
import com.example.vroom.fragments.ProfileRequestsFragment;
import com.example.vroom.fragments.RegisterDriverFragment;
import com.example.vroom.fragments.RideStatisticsFragment;
import com.example.vroom.fragments.UserRideHistoryFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

public class AdminActivity extends BaseActivity {

    private DrawerLayout drawer;
    private ImageButton logoButton;
    private ImageButton profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        setupToolbar();
        setupDrawer();
        loadDashboard();
        setupClickListeners();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawer.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
                    drawer.closeDrawer(androidx.core.view.GravityCompat.START);
                } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    finish();
                }
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); 

        logoButton = findViewById(R.id.logo);
        logoButton.setOnClickListener(v -> {
            loadDashboard();
        });

        profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(v -> {
            Toast.makeText(this, "Admin Panel", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_button);

        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_logout) {
                logout();
                return true;
            } else if (id == R.id.nav_panic_feed) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, new PanicFeedFragment())
                        .addToBackStack(null)
                        .commit();
                drawer.closeDrawer(androidx.core.view.GravityCompat.START);
                return true;
            }
            return false;
        });
    }

    private void loadDashboard() {

        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            getSupportFragmentManager().popBackStack();
        }

        FrameLayout contentFrame = findViewById(R.id.content_frame);
        contentFrame.removeAllViews();
        getLayoutInflater().inflate(
                R.layout.admin_dashboard_content,
                contentFrame,
                true
        );
        setupClickListeners();
    }

    private void setupClickListeners() {
        if (findViewById(R.id.cardManageUsers) != null) {
            findViewById(R.id.cardManageUsers).setOnClickListener(v -> {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, new BlockUserFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }

        if (findViewById(R.id.cardProfileRequests) != null) {
            findViewById(R.id.cardProfileRequests).setOnClickListener(v -> {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, new ProfileRequestsFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }

        if (findViewById(R.id.cardAddDriver) != null) {
            findViewById(R.id.cardAddDriver).setOnClickListener(v -> {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, new RegisterDriverFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }

        if(findViewById(R.id.cardReports)!=null){
            findViewById(R.id.cardReports).setOnClickListener(v -> {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, new RideStatisticsFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }

        if (findViewById(R.id.cardActiveRides) != null) {
            findViewById(R.id.cardActiveRides).setOnClickListener(v ->
                    startActivity(new Intent(this, MockActivity.class)));
        }

        if (findViewById(R.id.cardPastRides) != null) {
            findViewById(R.id.cardPastRides).setOnClickListener(v ->{
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new UserRideHistoryFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }

        if (findViewById(R.id.cardPanic) != null) {
            findViewById(R.id.cardPanic).setOnClickListener(v ->{
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new PanicFeedFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }

        if (findViewById(R.id.cardPricelist) != null) {
            findViewById(R.id.cardPricelist).setOnClickListener(v ->
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, new DefinePricelistFragment())
                        .addToBackStack(null)
                        .commit());
        }

        if (findViewById(R.id.cardChat) != null) {
            findViewById(R.id.cardChat).setOnClickListener(v ->
                    startActivity(new Intent(this, MockActivity.class)));
        }
    }

    private void logout() {
        StorageManager.getSharedPreferences(this);
        StorageManager.saveData("jwt", null);
        StorageManager.saveData("user_type", null);
        StorageManager.saveLong("expires", -1L);

        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}