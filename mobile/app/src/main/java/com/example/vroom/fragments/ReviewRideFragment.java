package com.example.vroom.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.ride.requests.LeaveReviewRequestDTO;
import com.example.vroom.R;
import com.example.vroom.activities.MainActivity;
import com.example.vroom.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewRideFragment extends DialogFragment {
    private Long rideId;
    private RatingBar driverRatingBar;
    private RatingBar carRatingBar;

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

    @Override
    public void onStart(){
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private int driverRating = 0;
    private int carRating = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_review, container, false);

        driverRatingBar = view.findViewById(R.id.driverRatingBar);
        carRatingBar = view.findViewById(R.id.carRatingBar);

        driverRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (b) {
                    driverRating = (int) v;
                }
            }
        });

        carRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (b) {
                    carRating = (int) v;
                }
            }
        });

        view.findViewById(R.id.btnClose).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        view.findViewById(R.id.btnSubmit).setOnClickListener(v -> {
            String comment = ((EditText) view.findViewById(R.id.commentInput)).getText().toString();
            submitReview(comment);
        });

        return view;
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
                    dismiss();
                    if (getActivity() instanceof MainActivity){
                        ((MainActivity) getActivity()).resetToHomeState();
                    }
                }
            }
            @Override
            public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                Toast.makeText(getContext(), "Error submitting review", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
