package com.example.vroom.fragments;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vroom.DTOs.ride.responses.RideHistoryResponseDTO;
import com.example.vroom.R;
import com.example.vroom.adapters.RideHistoryAdapter;
import com.example.vroom.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RideHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private RideHistoryAdapter adapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewRides);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RideHistoryAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        loadData();
        return view;
    }

    private void loadData(){
        RetrofitClient.getRideService().getRides(null, null, "startTime,desc")
            .enqueue(new Callback<List<RideHistoryResponseDTO>>() {
                @Override
                public void onResponse(Call<List<RideHistoryResponseDTO>> call, Response<List<RideHistoryResponseDTO>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        adapter.setRides(response.body());
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<RideHistoryResponseDTO>> call, Throwable t) {
                }
            });
    }

    public void updateRideList(List<RideHistoryResponseDTO> newList) {
        if (adapter != null) {
            adapter.setRides(newList);
            adapter.notifyDataSetChanged();
        }
    }
}
