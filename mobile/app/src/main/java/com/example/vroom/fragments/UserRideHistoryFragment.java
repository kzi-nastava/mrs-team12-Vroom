package com.example.vroom.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vroom.DTOs.ride.responses.RideResponseDTO;
import com.example.vroom.R;
import com.example.vroom.activities.MainActivity;
import com.example.vroom.adapters.UserRideHistoryAdapter;
import com.example.vroom.data.local.StorageManager;
import com.example.vroom.enums.RideStatus;
import com.example.vroom.viewmodels.UserRideHistoryViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserRideHistoryFragment extends Fragment
        implements UserRideHistoryAdapter.OnRideActionListener, android.hardware.SensorEventListener {

    private UserRideHistoryViewModel mViewModel;
    private RecyclerView recyclerView;
    private UserRideHistoryAdapter adapter;
    private EditText etUserEmail;
    private Button btnStartDate, btnEndDate, btnSearch, btnNextPage, btnPrevPage;
    private TextView tvPageIndicator;
    private Spinner spinnerSort;
    String[] sortOptions = {"Newest First", "Oldest First", "Price: High to Low", "Price: Low to High"};
    String[] sortFields = {"startTime,desc", "startTime,asc", "price,desc", "price,asc"};
    private String currentSort = "startTime,desc";
    private int currentPage = 0;

    private SensorManager sensorManager;
    private long lastShakeTime = 0;
    private static final float SHAKE_THRESHOLD = 2.7f;
    private String userType;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_ride_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StorageManager.getSharedPreferences(getActivity());
        userType = StorageManager.getData("user_type", "");

        mViewModel = new ViewModelProvider(requireActivity()).get(UserRideHistoryViewModel.class);

        sensorManager = (SensorManager) requireActivity().getSystemService(android.content.Context.SENSOR_SERVICE);

        recyclerView = view.findViewById(R.id.ride_history_rv);
        etUserEmail = view.findViewById(R.id.user_email_input);
        btnStartDate = view.findViewById(R.id.btn_start_date);
        btnEndDate = view.findViewById(R.id.btn_end_date);
        btnSearch = view.findViewById(R.id.btn_search);
        btnNextPage = view.findViewById(R.id.btn_next_page);
        btnPrevPage = view.findViewById(R.id.btn_prev_page);
        tvPageIndicator = view.findViewById(R.id.tvPageIndicator);
        spinnerSort = view.findViewById(R.id.spinner_sort);

        String userRole = StorageManager.getData("user_type", "");
        etUserEmail.setVisibility("ADMIN".equals(userRole) ? View.VISIBLE : View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserRideHistoryAdapter(new ArrayList<>(), userRole, this);
        recyclerView.setAdapter(adapter);

        ArrayAdapter<String> adapterSort = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sortOptions);
        adapterSort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapterSort);

        mViewModel.getRideHistoryLiveData().observe(getViewLifecycleOwner(), rides -> {
            adapter.setRides(rides != null ? rides : new ArrayList<>());
        });

        setupListeners();
        applyFilters();
    }

    @Override
    public void onMapClick(Long rideId) {
        List<RideResponseDTO> currentRides = mViewModel.getRideHistoryLiveData().getValue();

        if(currentRides == null) return;

        RideResponseDTO selectedRide = currentRides.stream()
                .filter(ride -> ride.getRideId().equals(rideId))
                .findFirst()
                .orElse(null);

        if(selectedRide == null || selectedRide.getRoute() == null) return;

        mViewModel.sendRideData(selectedRide);

        if (getActivity() == null) return;

        if(getActivity() instanceof MainActivity){
            getActivity().getSupportFragmentManager().beginTransaction()
                    .hide(this)
                    .addToBackStack("MAP_VIEW")
                    .commit();
        } else{
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            String rideJson = new Gson().toJson(selectedRide);
            intent.putExtra("ROUTE_DATA", rideJson);

            startActivity(intent);
        }
    }

    @Override
    public void onCardClick(RideResponseDTO ride) {
        displayPopup(ride);
    }

    private void displayPopup(RideResponseDTO ride) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_ride_details, null);
        setupPopupViews(dialogView, ride);

        new MaterialAlertDialogBuilder(requireContext(), R.style.CustomPopupTheme)
                .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.popup_background_rounded))
                .setView(dialogView)
                .setPositiveButton("Close", null)
                .show();
    }

    private void setupPopupViews(View view, RideResponseDTO info) {
        TextView tvId = view.findViewById(R.id.tvPopupRideId);
        TextView tvStatus = view.findViewById(R.id.tvPopupStatus);
        TextView tvPassengers = view.findViewById(R.id.tvPopupPassengers);

        TextView tvCancel = view.findViewById(R.id.tvPopupCancelReason);
        View layoutCancel = view.findViewById(R.id.layoutCancel);
        TextView tvComplaints = view.findViewById(R.id.tvPopupComplaints);
        View layoutComplaints = view.findViewById(R.id.layoutComplaints);

        View layoutRatingsFeedback = view.findViewById(R.id.layoutRatingsFeedback);
        TextView tvDriverRating = view.findViewById(R.id.tvDriverRating);
        TextView tvVehicleRating = view.findViewById(R.id.tvVehicleRating);
        TextView tvComment = view.findViewById(R.id.tvPopupComment);
        Button btnLeaveReview = view.findViewById(R.id.btnLeaveReview);

        tvId.setText("#" + info.getRideId());
        tvStatus.setText(info.getStatus().toString());
        applyStatusStyle(tvStatus, info.getStatus().toString());

        if (info.getPassengers() != null && !info.getPassengers().isEmpty()) {
            tvPassengers.setText(String.join("\n", info.getPassengers()));
        }

        if (info.getCancelReason() != null && !info.getCancelReason().isEmpty()) {
            layoutCancel.setVisibility(View.VISIBLE);
            tvCancel.setText(info.getCancelReason());
        }

        if (info.getComplaints() != null && !info.getComplaints().isEmpty()) {
            layoutComplaints.setVisibility(View.VISIBLE);
            tvComplaints.setText(String.join("\n", info.getComplaints()));
        }

        boolean hasFeedback = false;

        if (info.getDriverRating() != null && info.getDriverRating() > 0) {
            tvDriverRating.setVisibility(View.VISIBLE);
            tvDriverRating.setText("Driver: " + getStars(info.getDriverRating()));
            hasFeedback = true;
        }

        if (info.getVehicleRating() != null && info.getVehicleRating() > 0) {
            tvVehicleRating.setVisibility(View.VISIBLE);
            tvVehicleRating.setText("Vehicle: " + getStars(info.getVehicleRating()));
            hasFeedback = true;
        }

        if (info.getComment() != null && !info.getComment().trim().isEmpty()) {
            tvComment.setVisibility(View.VISIBLE);
            tvComment.setText("\"" + info.getComment() + "\"");
            hasFeedback = true;
        }

        if (hasFeedback) {
            layoutRatingsFeedback.setVisibility(View.VISIBLE);
        }

        boolean isRated = (info.getDriverRating() != null && info.getVehicleRating() != null);
        boolean withinThreeDays = info.getEndTime() != null &&
                info.getStatus().equals(RideStatus.FINISHED) &&
                LocalDateTime.now().minusDays(3).isBefore(info.getEndTime());
        boolean isUser = StorageManager.getData("user_type", "").equals("REGISTERED_USER");



        if(!isRated && withinThreeDays && isUser)
            btnLeaveReview.setVisibility(View.VISIBLE);
        else
            btnLeaveReview.setVisibility(View.GONE);
    }

    private String getStars(Integer rating) {
        int r = (rating == null) ? 0 : rating;
        return "★".repeat(r) + "☆".repeat(5 - r);
    }

    private void applyStatusStyle(TextView view, String status) {
        String s = status.toLowerCase();
        boolean isBad = s.contains("cancelled") || s.contains("rejected") || s.contains("denied");

        int bg = isBad ? R.color.cancelled_background : R.color.safe_background;
        int txt = isBad ? R.color.cancelled_text : R.color.safe_text;

        view.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), bg)));
        view.setTextColor(ContextCompat.getColor(requireContext(), txt));
    }

    private void setupListeners() {
        btnSearch.setOnClickListener(v -> { currentPage = 0; applyFilters(); });
        btnStartDate.setOnClickListener(v -> showDatePicker(btnStartDate));
        btnEndDate.setOnClickListener(v -> showDatePicker(btnEndDate));

        btnNextPage.setOnClickListener(v -> {
            if(adapter.getItemCount() >= 10) {
                currentPage++;
                updatePageAndFetch();
            }
        });

        btnPrevPage.setOnClickListener(v -> {
            if(currentPage > 0) {
                currentPage--;
                updatePageAndFetch();
            }
        });

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentSort = sortFields[i];

                currentPage = 0;
                updatePageAndFetch();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showDatePicker(Button button) {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, y, m, d) -> {
            button.setText(d + "." + (m + 1) + "." + y);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updatePageAndFetch() {
        tvPageIndicator.setText("Page " + (currentPage + 1));

        applyFilters();
    }

    private void applyFilters() {
        String email = etUserEmail.getVisibility() == View.VISIBLE ? etUserEmail.getText().toString().trim() : null;

        if (email != null && email.isEmpty()) email = null;

        String start = !btnStartDate.getText().toString().equals("Start Date") ? formatToISO(btnStartDate.getText().toString()) : null;
        String end = !btnEndDate.getText().toString().equals("End Date") ? formatToISO(btnEndDate.getText().toString()) : null;

        if(userType.equals("ADMIN"))
            mViewModel.fetchRideHistoryAdmin(email, currentSort, start, end, currentPage);
        else if(userType.equals("REGISTERED_USER"))
            mViewModel.fetchRideHistoryUser(currentSort, start, end, currentPage);
    }

    private String formatToISO(String dateStr) {
        try {
            String[] parts = dateStr.split("\\.");

            return parts[2] + "-" + String.format("%02d", Integer.parseInt(parts[1])) + "-" + String.format("%02d", Integer.parseInt(parts[0])) + "T00:00:00";
        } catch (Exception e) { return null; }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;

        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastShakeTime) < 1000) return;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        if (gForce > SHAKE_THRESHOLD) {
            lastShakeTime = currentTime;
            toggleDateSort();
        }
    }

    private void toggleDateSort() {
        if (currentSort.equals("startTime,desc")) {
            currentSort = "startTime,asc";
            spinnerSort.setSelection(1);

            Toast.makeText(getContext(), "Sorted by Date: Ascending", Toast.LENGTH_SHORT).show();
        } else {
            currentSort = "startTime,desc";
            spinnerSort.setSelection(0);

            Toast.makeText(getContext(), "Sorted by Date: Descending", Toast.LENGTH_SHORT).show();
        }

        currentPage = 0;
        updatePageAndFetch();
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {}

    @Override
    public void onResume() {
        super.onResume();
        lastShakeTime = System.currentTimeMillis();

        if (sensorManager != null) {
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER),
                    android.hardware.SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}