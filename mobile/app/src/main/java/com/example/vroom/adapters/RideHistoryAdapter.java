package com.example.vroom.adapters;
import com.example.vroom.R;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vroom.DTOs.ride.responses.RideHistoryResponseDTO;

import java.util.List;

public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.ViewHolder> {
    private List<RideHistoryResponseDTO> rides;

    public RideHistoryAdapter(List<RideHistoryResponseDTO> rides) {
        this.rides = rides;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_history_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RideHistoryResponseDTO ride = rides.get(position);

        holder.startLoc.setText(ride.getStartAddress());
        holder.endLoc.setText(ride.getEndAddress());
        holder.price.setText(String.format("%s RSD", ride.getPrice()));
        holder.status.setText(ride.getStatus().name());
        holder.dateTime.setText(ride.getStartTime().toString());

        if (ride.isPanicActivated()) {
            holder.safety.setText("PANIC");
            holder.safety.setBackgroundColor(Color.parseColor("#FFCDD2"));
            holder.safety.setTextColor(Color.RED);
        } else {
            holder.safety.setText("Safe");
            holder.safety.setBackgroundColor(Color.parseColor("#C8E6C9"));
            holder.safety.setTextColor(Color.parseColor("#2E7D32"));
        }
    }

    @Override
    public int getItemCount() {
        return rides != null ? rides.size() : 0;
    }

    public void setRides(List<RideHistoryResponseDTO> rides) {
        this.rides = rides;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView startLoc, endLoc, dateTime, price, status, safety;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            startLoc = itemView.findViewById(R.id.tvStartLocation);
            endLoc = itemView.findViewById(R.id.tvEndLocation);
            dateTime = itemView.findViewById(R.id.tvDateTime);
            price = itemView.findViewById(R.id.tvPrice);
            status = itemView.findViewById(R.id.tvStatus);
            safety = itemView.findViewById(R.id.tvSafety);
        }
    }
}
