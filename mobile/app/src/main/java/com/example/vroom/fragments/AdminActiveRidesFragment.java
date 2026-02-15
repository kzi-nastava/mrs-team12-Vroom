package com.example.vroom.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.EditText;

import com.example.vroom.R;
import com.example.vroom.activities.LoginActivity;
import com.example.vroom.activities.MainActivity;
import com.example.vroom.adapters.AdminActiveRidesAdapter;
import com.example.vroom.viewmodels.AdminActiveRidesViewModel;

import java.util.ArrayList;

public class AdminActiveRidesFragment extends Fragment {
    private AdminActiveRidesViewModel viewModel;
    private AdminActiveRidesAdapter adapter;

    public AdminActiveRidesFragment() { super(R.layout.fragment_admin_active_rides); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.ridesRecyclerView);
        EditText searchBar = view.findViewById(R.id.searchBar);

        viewModel = new ViewModelProvider(this).get(AdminActiveRidesViewModel.class);

        adapter = new AdminActiveRidesAdapter(new ArrayList<>(), rideId -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("RIDE_ID", rideId);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        viewModel.getFilteredRides().observe(getViewLifecycleOwner(), rides -> {
            adapter.updateData(rides);
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.filter(s.toString());
            }
            public void afterTextChanged(Editable s) {}
        });

        viewModel.loadRides();
    }
}
