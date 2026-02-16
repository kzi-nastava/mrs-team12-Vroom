package com.example.vroom.fragments;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.example.vroom.DTOs.ride.responses.RideHistoryMoreInfoResponseDTO;
import com.example.vroom.R;
import com.example.vroom.adapters.RideHistoryAdapter;
import com.example.vroom.viewmodels.RideHistoryViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.Calendar;

public class RideHistoryFragment extends Fragment implements RideHistoryAdapter.OnRideClickListener {
    private RideHistoryViewModel viewModel;
    private RideHistoryAdapter adapter;
    private Button btnStart, btnEnd, btnSearch, btnClear;
    private Spinner spinnerSort;
    private String[] sortOptions = {"Newest First", "Oldest First"};
    private String[] sortFields = {"startTime,desc", "startTime,asc"};
    private String currentSort = "startTime,desc";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_ride_history, container, false);
        initViews(view);
        setupRecyclerView(view);
        setupSortSpinner();
        return view;
    }

    private void initViews(View v) {
        btnStart = v.findViewById(R.id.btn_start_date);
        btnEnd = v.findViewById(R.id.btn_end_date);
        btnSearch = v.findViewById(R.id.btn_search);
        btnClear = v.findViewById(R.id.btn_clear_filters);
        spinnerSort = v.findViewById(R.id.spinner_sort);
    }

    private void setupRecyclerView(View v) {
        RecyclerView rv = v.findViewById(R.id.recyclerViewRides);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RideHistoryAdapter(this);
        rv.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        viewModel = new ViewModelProvider(this).get(RideHistoryViewModel.class);
        setupObservers();
        setupListeners();
        applyFilters();
    }

    private void setupObservers() {
        viewModel.getRides().observe(getViewLifecycleOwner(), rides -> {
            if (rides != null) adapter.setRides(rides);
        });
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        });
        viewModel.getSelectedRide().observe(getViewLifecycleOwner(), ride -> {
            if (ride != null) displayPopup(ride);
        });
    }

    private void setupListeners() {
        btnStart.setOnClickListener(v -> showDatePicker(btnStart));
        btnEnd.setOnClickListener(v -> showDatePicker(btnEnd));
        btnSearch.setOnClickListener(v -> applyFilters());
        btnClear.setOnClickListener(v -> {
            btnStart.setText("Start Date");
            btnEnd.setText("End Date");
            spinnerSort.setSelection(0);
            applyFilters();
        });
    }

    private void setupSortSpinner() {
        ArrayAdapter<String> adapterSort = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, sortOptions);
        adapterSort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapterSort);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSort = sortFields[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void showDatePicker(Button button) {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, y, m, d) -> {
            button.setText(String.format("%02d.%02d.%d", d, m + 1, y));
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void applyFilters() {
        String start = btnStart.getText().toString().equals("Start Date") ? null : formatToISO(btnStart.getText().toString());
        String end = btnEnd.getText().toString().equals("End Date") ? null : formatToISO(btnEnd.getText().toString());
        viewModel.fetchRideHistory(start, end, currentSort);
    }

    private String formatToISO(String dateStr) {
        try {
            String[] parts = dateStr.split("\\.");
            return parts[2] + "-" + String.format("%02d", Integer.parseInt(parts[1])) + "-" + String.format("%02d", Integer.parseInt(parts[0])) + "T00:00:00";
        } catch (Exception e) { return null; }
    }

    @Override
    public void onRideClick(Long rideId) {
        viewModel.fetchRideDetails(rideId);
    }

    private void displayPopup(RideHistoryMoreInfoResponseDTO info) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_ride_details, null);
        setupPopupViews(dialogView, info);
        new MaterialAlertDialogBuilder(requireContext(), R.style.CustomPopupTheme)
                .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.popup_background_rounded))
                .setView(dialogView)
                .setOnDismissListener(dialog -> viewModel.clearSelectedRide())
                .setPositiveButton("Close", null)
                .show();
    }

    private void setupPopupViews(View view, RideHistoryMoreInfoResponseDTO info) {
        TextView tvId = view.findViewById(R.id.tvPopupRideId);
        TextView tvStatus = view.findViewById(R.id.tvPopupStatus);
        TextView tvPassengers = view.findViewById(R.id.tvPopupPassengers);
        View layoutCancel = view.findViewById(R.id.layoutCancel);
        View layoutComplaints = view.findViewById(R.id.layoutComplaints);

        tvId.setText("#" + info.getRideID());
        tvStatus.setText(info.getStatus().toString());
        applyStatusStyle(tvStatus, info.getStatus().toString());

        if (info.getPassengers() != null) tvPassengers.setText(String.join("\n", info.getPassengers()));
        if (info.getCancelReason() != null) {
            layoutCancel.setVisibility(View.VISIBLE);
            ((TextView)view.findViewById(R.id.tvPopupCancelReason)).setText(info.getCancelReason());
        }
        if (info.getComplaints() != null) {
            layoutComplaints.setVisibility(View.VISIBLE);
            ((TextView)view.findViewById(R.id.tvPopupComplaints)).setText(String.join("\n", info.getComplaints()));
        }

        if (info.getDriverRating() != null || info.getComment() != null) {
            view.findViewById(R.id.layoutRatingsFeedback).setVisibility(View.VISIBLE);
            ((TextView)view.findViewById(R.id.tvDriverRating)).setText("Driver: " + getStars(info.getDriverRating()));
            ((TextView)view.findViewById(R.id.tvVehicleRating)).setText("Vehicle: " + getStars(info.getVehicleRating()));
            ((TextView)view.findViewById(R.id.tvPopupComment)).setText("\"" + info.getComment() + "\"");
        }
    }

    private String getStars(Integer rating) {
        int r = (rating == null) ? 0 : rating;
        return "★".repeat(r) + "☆".repeat(5 - r);
    }

    private void applyStatusStyle(TextView view, String status) {
        String s = status.toLowerCase();
        boolean bad = s.contains("cancelled") || s.contains("denied");
        view.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), bad ? R.color.cancelled_background : R.color.safe_background)));
        view.setTextColor(ContextCompat.getColor(requireContext(), bad ? R.color.cancelled_text : R.color.safe_text));
    }
}