package com.example.vroom.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.vroom.R;
import com.example.vroom.data.local.StorageManager;
import com.google.android.material.navigation.NavigationView;

public class AdminActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private void setupDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_logout) {
                logout();
                return true;
            }
            return false;
        });
    }

    private void loadDashboard() {
        getLayoutInflater().inflate(
                R.layout.admin_dashboard_content,
                findViewById(R.id.content_frame),
                true
        );
    }

    private void setupClickListeners() {
        findViewById(R.id.cardActiveRides).setOnClickListener(v ->
                startActivity(new Intent(this, MockActivity.class)));

        findViewById(R.id.cardPastRides).setOnClickListener(v ->
                startActivity(new Intent(this, MockActivity.class)));

        findViewById(R.id.cardPanic).setOnClickListener(v ->
                startActivity(new Intent(this, MockActivity.class)));

        findViewById(R.id.cardAddDriver).setOnClickListener(v ->
                startActivity(new Intent(this, MockActivity.class)));

        findViewById(R.id.cardManageUsers).setOnClickListener(v ->
                startActivity(new Intent(this, MockActivity.class)));

        findViewById(R.id.cardProfileRequests).setOnClickListener(v ->
                startActivity(new Intent(this, MockActivity.class)));

        findViewById(R.id.cardPricelist).setOnClickListener(v ->
                startActivity(new Intent(this, MockActivity.class)));

        findViewById(R.id.cardChat).setOnClickListener(v ->
                startActivity(new Intent(this, MockActivity.class)));

        findViewById(R.id.cardReports).setOnClickListener(v ->
                startActivity(new Intent(this, MockActivity.class)));
    }

    private void logout() {
        StorageManager.saveData("jwt", null);
        StorageManager.saveData("user_type", null);
        StorageManager.saveLong("expires", -1L);

        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        setupDrawer();
        loadDashboard();
        setupClickListeners();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawer.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
                    drawer.closeDrawer(androidx.core.view.GravityCompat.START);
                } else {
                    finish();
                }
            }
        });
    }
}