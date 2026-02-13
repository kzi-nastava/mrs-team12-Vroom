package com.example.vroom.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.vroom.DTOs.ride.responses.GetRideResponseDTO;
import com.example.vroom.R;
import com.example.vroom.adapters.ActiveRidesAdapter;
import com.example.vroom.viewmodels.ActiveRidesViewModel;

import java.util.ArrayList;
import java.util.List;


public class ActiveRidesFragment extends Fragment implements ActiveRidesAdapter.OnRideActionListener {

    private ActiveRidesViewModel viewModel;
    private ActiveRidesAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvEmptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_rides, container, false);

        recyclerView = view.findViewById(R.id.rvActiveRides);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ActiveRidesAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ActiveRidesViewModel.class);

        viewModel.getRides().observe(getViewLifecycleOwner(), rides -> {
            if (rides != null) {
                if (rides.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    tvEmptyState.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    tvEmptyState.setVisibility(View.GONE);
                    adapter.updateRides(rides);
                }
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            swipeRefresh.setRefreshing(isLoading != null && isLoading);
        });

        swipeRefresh.setOnRefreshListener(() -> viewModel.fetchActiveRides());

        viewModel.fetchActiveRides();
    }

    @Override
    public void onStartRide(GetRideResponseDTO ride) {
        viewModel.startRide(ride.getRideID());
    }

    @Override
    public void onCancelRide(GetRideResponseDTO ride) {
        Toast.makeText(getContext(), "Cancel functionality not implemented yet", Toast.LENGTH_SHORT).show();
    }
}