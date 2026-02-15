package com.example.vroom.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.ride.requests.LeaveReviewRequestDTO;
import com.example.vroom.R;
import com.example.vroom.activities.MainActivity;
import com.example.vroom.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewRideFragment extends Fragment {
    private Long rideId;

    public static ReviewRideFragment newInstance(Long rideId) {
        ReviewRideFragment fragment = new ReviewRideFragment();
        Bundle args = new Bundle();
        args.putLong("ride_id", rideId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) rideId = getArguments().getLong("ride_id");
    }

    private int driverRating = 0;
    private int carRating = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_review, container, false);

        setupStarGroup(view.findViewById(R.id.driverStars), true);
        setupStarGroup(view.findViewById(R.id.carStars), false);

        view.findViewById(R.id.btnClose).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        view.findViewById(R.id.btnSubmit).setOnClickListener(v -> {
            String comment = ((EditText) view.findViewById(R.id.commentInput)).getText().toString();
            submitReview(comment);
        });

        return view;
    }

    private void setupStarGroup(LinearLayout layout, boolean isDriver) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            final int rating = i + 1;
            View star = layout.getChildAt(i);
            star.setOnClickListener(v -> updateStarRating(layout, rating, isDriver));
        }
    }

    private void updateStarRating(LinearLayout layout, int rating, boolean isDriver) {
        if (isDriver) driverRating = rating; else carRating = rating;

        for (int i = 0; i < layout.getChildCount(); i++) {
            ImageView star = (ImageView) layout.getChildAt(i);
            if (i < rating) {
                star.setImageResource(android.R.drawable.btn_star_big_on);
                star.setColorFilter(Color.parseColor("#99C2A2"));
            } else {
                star.setImageResource(android.R.drawable.btn_star_big_off);
                star.setColorFilter(Color.parseColor("#D1D5DB"));
            }
        }
    }

    private void submitReview(String comment) {
        if (driverRating == 0 || carRating == 0) {
            Toast.makeText(getContext(), "Please rate both the driver and the car", Toast.LENGTH_SHORT).show();
            return;
        }

        LeaveReviewRequestDTO request = new LeaveReviewRequestDTO(driverRating, carRating, comment);
        RetrofitClient.getRideService().leaveReview(rideId, request).enqueue(new Callback<MessageResponseDTO>() {
            @Override
            public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Thank you for your review!", Toast.LENGTH_SHORT).show();
                    returnToHomeState();
                }
            }
            @Override
            public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                Toast.makeText(getContext(), "Error submitting review", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void returnToHomeState() {
        if (isAdded() && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).resetToHomeState();
        }
    }
}
