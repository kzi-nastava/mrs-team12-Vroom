package com.example.vroom.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vroom.DTOs.ride.responses.RideResponseDTO;
import com.example.vroom.R;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserRideHistoryAdapter extends RecyclerView.Adapter<UserRideHistoryAdapter.RideViewHolder> {

    private List<RideResponseDTO> rideList;
    private String currentUserType;
    private OnRideActionListener listener;

    public interface OnRideActionListener {
        void onMapClick(Long rideId);
        void onCardClick(RideResponseDTO ride);
    }

    public UserRideHistoryAdapter(List<RideResponseDTO> rideList, String currentUserType, OnRideActionListener listener) {
        this.rideList = rideList;
        this.currentUserType = currentUserType;
        this.listener = listener;
    }

    public void setRides(List<RideResponseDTO> rides){
        this.rideList = rides;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ride_history_card, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        RideResponseDTO ride = rideList.get(position);

        holder.tvStart.setText(ride.getRoute().getStartAddress());
        holder.tvEnd.setText(ride.getRoute().getEndAddress());

        if (ride.getStartTime() != null) {
            DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
            String formatted = ride.getStartTime().format(displayFormatter);
            holder.tvDate.setText(formatted);
        } else {
            holder.tvDate.setText("N/A");
        }

        holder.tvPrice.setText(String.format("%.2f EUR", ride.getPrice()));
        holder.tvStatus.setText(ride.getStatus().toString());

        if (ride.getPanicActivated()) {
            holder.tvSafety.setText("Panic");
            holder.tvSafety.setTextColor(Color.parseColor("#E64A19"));
        } else {
            holder.tvSafety.setText("Safe");
            holder.tvSafety.setTextColor(Color.parseColor("#2E7D32"));
        }

        boolean canViewMap = "ADMIN".equals(currentUserType) ||
                "DRIVER".equals(currentUserType) ||
                "REGISTERED_USER".equals(currentUserType);

        if (canViewMap) {
            holder.btnMap.setVisibility(View.VISIBLE);
            holder.btnMap.setOnClickListener(v -> {
                if (listener != null) listener.onMapClick(ride.getRideId());
            });
        } else {
            holder.btnMap.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCardClick(ride);
        });
    }

    @Override
    public int getItemCount() {
        return (rideList != null) ? rideList.size() : 0;
    }

    static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView tvStart, tvEnd, tvDate, tvPrice, tvStatus, tvSafety;
        Button btnMap;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStart = itemView.findViewById(R.id.tvStartLocation);
            tvEnd = itemView.findViewById(R.id.tvEndLocation);
            tvDate = itemView.findViewById(R.id.tvDateTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvSafety = itemView.findViewById(R.id.tvSafety);
            btnMap = itemView.findViewById(R.id.btnCheckMap);
        }
    }
}