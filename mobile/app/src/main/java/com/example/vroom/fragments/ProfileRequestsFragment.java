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
import com.example.vroom.adapters.ProfileRequestsAdapter;
import com.example.vroom.viewmodels.AdminViewModel;

public class ProfileRequestsFragment extends Fragment {

    private AdminViewModel viewModel;
    private RecyclerView recyclerView;
    private ProfileRequestsAdapter adapter;
    private TextView errorMessage;
    private TextView successMessage;

    public ProfileRequestsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.requests_recycler);
        errorMessage = view.findViewById(R.id.error_message);
        successMessage = view.findViewById(R.id.success_message);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ProfileRequestsAdapter(
                (requestId) -> viewModel.approveRequest(requestId),
                (requestId, comment) -> viewModel.rejectRequest(requestId, comment)
        );

        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        observeViewModel();
        viewModel.loadProfileRequests();
    }

    private void observeViewModel() {
        viewModel.getProfileRequests().observe(getViewLifecycleOwner(), requests -> {
            if (requests != null) {
                adapter.setRequests(requests);
            }
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                errorMessage.setText(error);
                errorMessage.setVisibility(View.VISIBLE);
                successMessage.setVisibility(View.GONE);
                errorMessage.postDelayed(() -> {
                    errorMessage.setVisibility(View.GONE);
                }, 3000);
            }
        });

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), success -> {
            if (success != null && !success.isEmpty()) {
                successMessage.setText(success);
                successMessage.setVisibility(View.VISIBLE);
                errorMessage.setVisibility(View.GONE);
                successMessage.postDelayed(() -> {
                    successMessage.setVisibility(View.GONE);
                }, 3000);
            }
        });
    }
}
