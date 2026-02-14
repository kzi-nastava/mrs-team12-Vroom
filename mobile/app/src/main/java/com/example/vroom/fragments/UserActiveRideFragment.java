package com.example.vroom.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vroom.R;
import com.example.vroom.activities.MainActivity;
import com.example.vroom.adapters.UserActiveRidesAdapter;
import com.example.vroom.viewmodels.UserActiveRideViewModel;

public class UserActiveRideFragment extends Fragment implements UserActiveRidesAdapter.OnRideClickListener {
    private UserActiveRideViewModel viewModel;
    private UserActiveRidesAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyStateMsg;
    private View loadingView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_active_rides, container, false);
        recyclerView = view.findViewById(R.id.recycler_rides);
        emptyStateMsg = view.findViewById(R.id.text_empty_state);
        loadingView = view.findViewById(R.id.loading_progress);
        adapter = new UserActiveRidesAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        viewModel = new ViewModelProvider(this).get(UserActiveRideViewModel.class);
        setupObservers();
        viewModel.loadActiveRides();
        return view;
    }

    private void setupObservers() {
        viewModel.getActiveRides().observe(getViewLifecycleOwner(), rides -> {
            adapter.submitList(rides);
            emptyStateMsg.setVisibility((rides == null || rides.isEmpty()) ? View.VISIBLE : View.GONE);
        });
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> loadingView.setVisibility(loading ? View.VISIBLE : View.GONE));
    }

    @Override
    public void onTrackRide(Long rideId) {
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            activity.updateUIForRideState(rideId);
            activity.getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onCancelRide(Long rideId) {}
}