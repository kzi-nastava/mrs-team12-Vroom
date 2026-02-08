package com.example.vroom.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class RideHistoryFragment extends Fragment implements RideHistoryAdapter.OnRideClickListener{
    private RideHistoryViewModel viewModel;
    private RideHistoryAdapter adapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle bundle){
        View view = inflater.inflate(R.layout.fragment_ride_history, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewRides);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RideHistoryAdapter(this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle){
        super.onViewCreated(view, bundle);
        viewModel = new ViewModelProvider(this).get(RideHistoryViewModel.class);

        viewModel.getRides().observe(getViewLifecycleOwner(), rides -> {
            if (rides != null){
                adapter.setRides(rides);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null){
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getSelectedRide().observe(getViewLifecycleOwner(), rideInfo -> {
            if (rideInfo != null) {
                displayPopup(rideInfo);
            }
        });

        viewModel.fetchRideHistory("startTime,desc");
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

        TextView tvCancel = view.findViewById(R.id.tvPopupCancelReason);
        View layoutCancel = view.findViewById(R.id.layoutCancel);
        TextView tvComplaints = view.findViewById(R.id.tvPopupComplaints);
        View layoutComplaints = view.findViewById(R.id.layoutComplaints);

        View layoutRatingsFeedback = view.findViewById(R.id.layoutRatingsFeedback);
        TextView tvDriverRating = view.findViewById(R.id.tvDriverRating);
        TextView tvVehicleRating = view.findViewById(R.id.tvVehicleRating);
        TextView tvComment = view.findViewById(R.id.tvPopupComment);

        tvId.setText("#" + info.getRideID());
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
    }
    private String getStars(Integer rating) {
        int r = (rating == null) ? 0 : rating;
        return "★".repeat(r) + "☆".repeat(5 - r);
    }

    private void applyStatusStyle(TextView view, String status) {
        boolean isActionable = status.toLowerCase().contains("cancelled") || status.toLowerCase().contains("denied");
        int bg = isActionable ? R.color.cancelled_background : R.color.safe_background;
        int txt = isActionable ? R.color.cancelled_text : R.color.safe_text;

        view.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), bg)));
        view.setTextColor(ContextCompat.getColor(requireContext(), txt));
    }


}
