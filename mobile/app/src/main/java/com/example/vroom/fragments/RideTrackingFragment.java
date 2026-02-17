package com.example.vroom.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.route.responses.GetRouteResponseDTO;
import com.example.vroom.R;
import com.example.vroom.activities.RideNavigationListener;
import com.example.vroom.network.RetrofitClient;
import com.example.vroom.viewmodels.MainViewModel;
import com.example.vroom.viewmodels.RideTrackingViewModel;
import com.google.android.gms.location.LocationServices;

public class RideTrackingFragment extends Fragment {
    private static final String ARG_RIDE_ID = "ride_id";
    private static final String ARG_USER_ROLE = "user_role";
    private RideTrackingViewModel viewModel;
    private MainViewModel mainViewModel;
    private Long rideId;
    private String userRole;
    private TextView etaText, startAddress, endAddress;
    private Button btnFinishRide, btnSubmitComplaint, btnPanic, btnStopRide;
    private EditText editComplaint;
    private LinearLayout userActions, driverActions;

    public static RideTrackingFragment newInstance(Long rideId, String userType) {
        RideTrackingFragment fragment = new RideTrackingFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RIDE_ID, rideId);
        args.putString(ARG_USER_ROLE, userType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rideId = getArguments().getLong(ARG_RIDE_ID);
            userRole = getArguments().getString(ARG_USER_ROLE);
        }
        viewModel = new ViewModelProvider(requireActivity()).get(RideTrackingViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_tracking, container, false);
        initViews(view);
        setupUI();
        setupObservers();

        mainViewModel.setRideTrackingActive(true, userRole);
        viewModel.loadRoute(rideId);
        viewModel.subscribeToRideUpdates(rideId);

        if ("DRIVER".equals(userRole)) {
            viewModel.startTracking(LocationServices.getFusedLocationProviderClient(requireActivity()), rideId);
        }
        return view;
    }

    private void initViews(View view) {
        etaText = view.findViewById(R.id.text_eta);
        startAddress = view.findViewById(R.id.text_start_address);
        endAddress = view.findViewById(R.id.text_end_address);
        userActions = view.findViewById(R.id.section_user_actions);
        driverActions = view.findViewById(R.id.section_driver_actions);
        btnFinishRide = view.findViewById(R.id.btn_finish_ride);
        btnSubmitComplaint = view.findViewById(R.id.btn_submit_complaint);
        editComplaint = view.findViewById(R.id.edit_complaint);
        btnPanic = view.findViewById(R.id.btn_panic);
        btnStopRide = view.findViewById(R.id.btn_stop_ride);
    }

    private void setupUI() {
        if ("DRIVER".equals(userRole)) {
            driverActions.setVisibility(View.VISIBLE);
            userActions.setVisibility(View.GONE);
            btnStopRide.setVisibility(View.VISIBLE);
        } else if ("REGISTERED_USER".equals(userRole)) {
            userActions.setVisibility(View.VISIBLE);
            driverActions.setVisibility(View.GONE);
            btnStopRide.setVisibility(View.GONE);
        } else {
            userActions.setVisibility(View.GONE);
            driverActions.setVisibility(View.GONE);
            btnPanic.setVisibility(View.GONE);
        }
        btnFinishRide.setOnClickListener(v -> {
            if ("DRIVER".equals(userRole)) {
                viewModel.finishRide(rideId);
            }
        });

        btnSubmitComplaint.setOnClickListener(v -> {
            String complaint = editComplaint.getText().toString();
            if (!complaint.isEmpty()) {
                viewModel.sendComplaint(rideId, complaint);
                editComplaint.setText("");
            }
        });

        btnPanic.setOnClickListener(v -> {
            PanicFragment frag = PanicFragment.newInstance(rideId);

            frag.show(
                    requireActivity().getSupportFragmentManager(),
                    "PanicFragment"
            );
        });

        btnStopRide.setOnClickListener(v -> {
            StopRideFragment frag = StopRideFragment.newInstance(rideId);
            frag.show(
                    requireActivity().getSupportFragmentManager(),
                    "StopRideFragment"
            );

        });
    }

    private void setupObservers() {
        viewModel.getActiveRoute().observe(getViewLifecycleOwner(), this::updateRouteUI);
        viewModel.getRideUpdate().observe(getViewLifecycleOwner(), update -> {
            if (update != null) etaText.setText("ETA : " + update.getTimeLeft().intValue() + " minutes");
        });
        viewModel.getComplaintSent().observe(getViewLifecycleOwner(), isSent -> {
            if (isSent){
                Toast.makeText(getContext(), viewModel.getMessage().getValue(), Toast.LENGTH_SHORT).show();
                viewModel.setComplaintSent(false);
            }
        });
    }

    private void navigateToMain() {
        if (isAdded()) requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void updateRouteUI(GetRouteResponseDTO route) {
        if (route == null) return;
        startAddress.setText(route.getStartAddress());
        endAddress.setText(route.getEndAddress());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.unsubscribeFromRideUpdates();
        mainViewModel.setRideTrackingActive(false, userRole);
    }
}