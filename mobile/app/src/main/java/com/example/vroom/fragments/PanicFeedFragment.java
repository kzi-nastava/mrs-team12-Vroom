package com.example.vroom.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vroom.R;
import com.example.vroom.adapters.PanicAdapter;
import com.example.vroom.databinding.FragmentPanicFeedBinding;
import com.example.vroom.viewmodels.PanicFeedViewModel;

import java.util.ArrayList;

public class PanicFeedFragment extends Fragment implements PanicAdapter.OnPanicClickListener {

    private PanicFeedViewModel mViewModel;
    private FragmentPanicFeedBinding binding;
    private PanicAdapter adapter;

    public static PanicFeedFragment newInstance() {
        return new PanicFeedFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPanicFeedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PanicFeedViewModel.class);

        setupRecyclerView();
        observeViewModel();

        mViewModel.loadAlerts();
    }

    private void setupRecyclerView() {
        adapter = new PanicAdapter(new ArrayList<>(), this);
        binding.panicAlerts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.panicAlerts.setAdapter(adapter);
    }

    private void observeViewModel() {
        mViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.panicFeedSpinner.setVisibility(isLoading ? View.VISIBLE : View.GONE);

            if(isLoading){
                binding.noAlertsLabel.setVisibility(View.GONE);
            }else{
                binding.noAlertsLabel.setVisibility(View.VISIBLE);
            }
        });

        mViewModel.getPanicAlerts().observe(getViewLifecycleOwner(), alerts -> {
            if (alerts != null) {
                adapter.updateData(alerts);
                binding.noAlertsLabel.setVisibility(alerts.isEmpty() ? View.VISIBLE : View.GONE);

                boolean isLoading = mViewModel.getIsLoading().getValue();
                if (!isLoading && alerts.isEmpty()) {
                    binding.noAlertsLabel.setVisibility(View.VISIBLE);
                } else {
                    binding.noAlertsLabel.setVisibility(View.GONE);
                }
            }
        });

        mViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), success -> {
            if(success != null){
                Toast.makeText(getContext(), success, Toast.LENGTH_SHORT).show();
            }
        });

        mViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapClick(Long rideId) {
        // map redirect here
        // get data and redirect to map
        Toast.makeText(getContext(), "Showing ride #" + rideId + " on map", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResolveClick(Long alertId, int position) {
        mViewModel.resolvePanic(alertId, position);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}