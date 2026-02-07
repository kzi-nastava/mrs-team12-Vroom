package com.example.vroom.fragments;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vroom.R;
import com.example.vroom.viewmodels.StopRideViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class StopRideFragment extends BottomSheetDialogFragment {

    private StopRideViewModel mViewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private static final String ARG_RIDE_ID = "ride_id";
    private Long rideId;


    public static StopRideFragment newInstance(Long rideId) {
        StopRideFragment fragment = new StopRideFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RIDE_ID, rideId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.rideId = getArguments().getLong(ARG_RIDE_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stop_ride, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(StopRideViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        ViewGroup rootContainer = view.findViewById(R.id.stop_ride_root);
        Button btnConfirm = view.findViewById(R.id.btn_confirm);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        ProgressBar spinner = view.findViewById(R.id.loading_spinner);
        TextView title = view.findViewById(R.id.stop_title);

        btnConfirm.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(rootContainer);

            btnConfirm.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            title.setText("Stopping....");

            requestLocationAndStop();
        });

        btnCancel.setOnClickListener(v -> dismiss());

        observeViewModel();
    }

    private void observeViewModel() {
        mViewModel.getStopSuccess().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                Toast.makeText(getContext(), "Ride stopped", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        mViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    private void requestLocationAndStop() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            double lat = 0;
            double lng = 0;

            if (location != null) {
                lat = location.getLatitude();
                lng = location.getLongitude();


                mViewModel.stopRideWithLocation(rideId, lat, lng);
            }else{
                Toast.makeText(requireContext(), "Location unavailable, please activate location permission", Toast.LENGTH_SHORT).show();
            }
        });
    }
}