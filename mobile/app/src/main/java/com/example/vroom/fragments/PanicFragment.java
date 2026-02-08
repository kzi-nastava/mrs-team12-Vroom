package com.example.vroom.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vroom.R;
import com.example.vroom.data.local.StorageManager;
import com.example.vroom.viewmodels.PanicViewModel;
import com.example.vroom.viewmodels.StopRideViewModel;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PanicFragment extends BottomSheetDialogFragment {

    private PanicViewModel mViewModel;

    private static final String ARG_RIDE_ID = "ride_id";
    private Long rideId;

    public static PanicFragment newInstance(Long rideId) {
        PanicFragment fragment = new PanicFragment();
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

        StorageManager.saveData("jwt","eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYXJrby5tYXJrb3ZpYzFAZ21haWwuY29tIiwiaWQiOjMsInR5cGUiOiJEcml2ZXIiLCJpYXQiOjE3NzA1NDg3MzQsImV4cCI6MTgwMjA4NDczNH0.vevRgSPLijBcfq5CjxpX8YzP2lXYF1dWnfMWTVptDMA");
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_panic, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PanicViewModel.class);

        ViewGroup rootContainer = view.findViewById(R.id.panic_root);
        Button btnConfirm = view.findViewById(R.id.btn_send_panic);
        Button btnCancel = view.findViewById(R.id.btn_panic_cancel);

        ProgressBar spinner = view.findViewById(R.id.panic_loading_spinner);
        TextView title = view.findViewById(R.id.panic_title);

        btnConfirm.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(rootContainer);

            btnConfirm.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            title.setText("Notifying....");

            mViewModel.panicNotificationRequest(this.rideId);
        });

        btnCancel.setOnClickListener(v -> dismiss());

        observeViewModel();
    }

    private void observeViewModel() {
        mViewModel.getPanicSuccess().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
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


}