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

import com.example.vroom.DTOs.ride.requests.FavoriteRouteDTO;
import com.example.vroom.R;
import com.example.vroom.DTOs.ride.requests.OrderFromFavoriteRequestDTO;
import com.example.vroom.adapters.FavoriteRoutesAdapter;
import com.example.vroom.enums.VehicleType;
import com.example.vroom.viewmodels.OrderFromFavoritesViewModel;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class OrderFromFavoritesFragment extends Fragment implements FavoriteRoutesAdapter.OnFavoriteActionListener {

    private OrderFromFavoritesViewModel viewModel;
    private FavoriteRoutesAdapter adapter;
    private RecyclerView recyclerFavorites;
    private TextView errorMessage, loadingState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_from_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(OrderFromFavoritesViewModel.class);

        initViews(view);
        setupRecyclerView();
        observeViewModel();

        viewModel.loadFavoriteRoutes();
    }

    private void initViews(View view) {
        recyclerFavorites = view.findViewById(R.id.recycler_favorites);
        errorMessage = view.findViewById(R.id.error_message);
        loadingState = view.findViewById(R.id.loading_state);
    }

    private void setupRecyclerView() {
        adapter = new FavoriteRoutesAdapter(new ArrayList<>(), this);
        recyclerFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerFavorites.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getFavoriteRoutes().observe(getViewLifecycleOwner(), favorites -> {
            if (favorites != null && !favorites.isEmpty()) {
                recyclerFavorites.setVisibility(View.VISIBLE);
                loadingState.setVisibility(View.GONE);
                adapter.updateFavorites(favorites);
            } else {
                recyclerFavorites.setVisibility(View.GONE);
                loadingState.setVisibility(View.VISIBLE);
                loadingState.setText("No favorite routes found");
            }
        });

        viewModel.getOrderedRide().observe(getViewLifecycleOwner(), ride -> {
            if (ride != null) {
                Toast.makeText(getContext(), "Ride ordered successfully! ID: #" + ride.getRideID(), Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                errorMessage.setText(error);
                errorMessage.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            } else {
                errorMessage.setVisibility(View.GONE);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                loadingState.setVisibility(View.VISIBLE);
                loadingState.setText("Loading...");
            } else {
                loadingState.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onUseRoute(FavoriteRouteDTO favorite, VehicleType vehicleType,
                           Boolean babiesAllowed, Boolean petsAllowed, LocalDateTime scheduledTime) {

        OrderFromFavoriteRequestDTO request = new OrderFromFavoriteRequestDTO(
                favorite.getId(),
                vehicleType,
                babiesAllowed,
                petsAllowed,
                scheduledTime
        );

        viewModel.orderFromFavorite(request);
    }

    @Override
    public void onDeleteRoute(FavoriteRouteDTO favorite) {
        viewModel.removeFavorite(favorite.getId());
    }
}