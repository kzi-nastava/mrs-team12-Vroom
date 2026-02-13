package com.example.vroom.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vroom.DTOs.ride.requests.FavoriteRouteDTO;
import com.example.vroom.R;
import com.example.vroom.enums.VehicleType;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class FavoriteRoutesAdapter extends RecyclerView.Adapter<FavoriteRoutesAdapter.FavoriteViewHolder> {

    private List<FavoriteRouteDTO> favorites;
    private OnFavoriteActionListener listener;

    public interface OnFavoriteActionListener {
        void onUseRoute(FavoriteRouteDTO favorite, VehicleType vehicleType,
                        Boolean babiesAllowed, Boolean petsAllowed, LocalDateTime scheduledTime);
        void onDeleteRoute(FavoriteRouteDTO favorite);
    }

    public FavoriteRoutesAdapter(List<FavoriteRouteDTO> favorites, OnFavoriteActionListener listener) {
        this.favorites = favorites;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_route, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FavoriteRouteDTO favorite = favorites.get(position);
        holder.bind(favorite, listener);
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public void updateFavorites(List<FavoriteRouteDTO> newFavorites) {
        this.favorites = newFavorites;
        notifyDataSetChanged();
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView tvStartAddress, tvEndAddress, tvRouteName;
        RadioGroup vehicleGroup;
        RadioButton radioStandard, radioLuxury, radioMinivan;
        CheckBox checkboxKids, checkboxPets;
        TimePicker timePicker;
        Button btnUseRoute, btnDeleteRoute;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStartAddress = itemView.findViewById(R.id.start_address);
            tvEndAddress = itemView.findViewById(R.id.end_address);
            tvRouteName = itemView.findViewById(R.id.route_name);
            vehicleGroup = itemView.findViewById(R.id.vehicle_group);
            radioStandard = itemView.findViewById(R.id.radio_standard);
            radioLuxury = itemView.findViewById(R.id.radio_luxury);
            radioMinivan = itemView.findViewById(R.id.radio_minivan);
            checkboxKids = itemView.findViewById(R.id.checkbox_kids);
            checkboxPets = itemView.findViewById(R.id.checkbox_pets);
            timePicker = itemView.findViewById(R.id.time_picker);
            btnUseRoute = itemView.findViewById(R.id.btn_use_route);
            btnDeleteRoute = itemView.findViewById(R.id.btn_delete_route);
        }

        public void bind(FavoriteRouteDTO favorite, OnFavoriteActionListener listener) {
            tvStartAddress.setText(favorite.getStartAddress() != null ? favorite.getStartAddress() : "N/A");
            tvEndAddress.setText(favorite.getEndAddress() != null ? favorite.getEndAddress() : "N/A");
            tvRouteName.setText(favorite.getName() != null ? favorite.getName() : "Unnamed Route");


            radioStandard.setChecked(true);
            timePicker.setIs24HourView(true);

            btnUseRoute.setOnClickListener(v -> {
                VehicleType vehicleType = getSelectedVehicleType();
                Boolean babiesAllowed = checkboxKids.isChecked();
                Boolean petsAllowed = checkboxPets.isChecked();
                LocalDateTime scheduledTime = getScheduledTime();

                listener.onUseRoute(favorite, vehicleType, babiesAllowed, petsAllowed, scheduledTime);
            });

            btnDeleteRoute.setOnClickListener(v -> listener.onDeleteRoute(favorite));
        }

        private VehicleType getSelectedVehicleType() {
            int selectedId = vehicleGroup.getCheckedRadioButtonId();
            if (selectedId == radioLuxury.getId()) {
                return VehicleType.LUXURY;
            } else if (selectedId == radioMinivan.getId()) {
                return VehicleType.MINIVAN;
            } else {
                return VehicleType.STANDARD;
            }
        }

        private LocalDateTime getScheduledTime() {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime scheduled = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0);


            if (scheduled.isBefore(now)) {
                scheduled = scheduled.plusDays(1);
            }

            if (scheduled.isAfter(now.plusHours(5))) {
                return null;
            }

            if (scheduled.isBefore(now.plusMinutes(10))) {
                return null;
            }

            return scheduled;
        }
    }
}