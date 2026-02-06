package com.example.vroom.fragments;

import androidx.lifecycle.ViewModelProvider;

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
import com.example.vroom.viewmodels.CancelRideViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CancelRideFragment extends BottomSheetDialogFragment {

    private CancelRideViewModel mViewModel;
    private static final String ARG_RIDE_ID = "ride_id";
    private Long rideId;
    private String userType;

    public static CancelRideFragment newInstance(Long rideId) {
        CancelRideFragment fragment = new CancelRideFragment();
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

        this.userType = StorageManager.getData("user_type", "");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cancel_ride, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CancelRideViewModel.class);

        TextInputLayout reasonContainer = view.findViewById(R.id.reason);

        ViewGroup rootContainer = view.findViewById(R.id.cancel_ride_root);
        Button btnConfirm = view.findViewById(R.id.btn_confirm);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        ProgressBar spinner = view.findViewById(R.id.loading_spinner);
        TextView title = view.findViewById(R.id.cancel_title);

        if ("DRIVER".equals(this.userType)) {
            reasonContainer.setVisibility(View.VISIBLE);
        } else {
            reasonContainer.setVisibility(View.GONE);
        }

        btnConfirm.setOnClickListener(v -> {
            cancelRide(reasonContainer, rootContainer, btnConfirm, btnCancel, spinner, title);
        });

        btnCancel.setOnClickListener(v -> dismiss());

        observeViewModel();
    }

    private void cancelRide(
            TextInputLayout reasonContainer,
            ViewGroup rootContainer,
            Button btnConfirm,
            Button btnCancel,
            ProgressBar spinner,
            TextView title
    ){
        String reason = "";

        if("DRIVER".equals(userType) && reasonContainer.getEditText() != null){
            reason = reasonContainer.getEditText().getText().toString();

            if (reason.isEmpty()) {
                reasonContainer.setError("Please provide a reason");
                return;
            }
        }

        TransitionManager.beginDelayedTransition(rootContainer);

        btnConfirm.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);
        title.setText("Cancelling....");

        mViewModel.cancelRide(this.rideId, reason);
    }

    private void observeViewModel() {
        mViewModel.getCancelSuccess().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                Toast.makeText(getContext(), "Ride cancelled", Toast.LENGTH_SHORT).show();
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