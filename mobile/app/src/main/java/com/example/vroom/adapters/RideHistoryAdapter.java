package com.example.vroom.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vroom.DTOs.ride.responses.RideHistoryResponseDTO;
import com.example.vroom.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.RideViewHolder> {
    private List<RideHistoryResponseDTO> rides = new ArrayList<>();
    private final OnRideClickListener listener;

    public interface OnRideClickListener {
        void onRideClick(Long rideId);
    }
    public RideHistoryAdapter(OnRideClickListener listener){
        this.listener = listener;
    }
    public void setRides(List<RideHistoryResponseDTO> rides) {
        this.rides = rides;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_history_card, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        RideHistoryResponseDTO ride = rides.get(position);
        Context context = holder.itemView.getContext();

        holder.tvStart.setText(ride.getStartAddress());
        holder.tvEnd.setText(ride.getEndAddress());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. • HH:mm");
        holder.tvDate.setText(ride.getStartTime().format(formatter));

        holder.tvPrice.setText(String.format("%.2f RSD", ride.getPrice()));

        String status = ride.getStatus().toString();
        holder.tvStatus.setText(status);

        if ("CANCELLED".equalsIgnoreCase(status) || "REJECTED".equalsIgnoreCase(status)) {
            holder.tvStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.cancelled_background)));
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.cancelled_text));
        } else {
            holder.tvStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.safe_background)));
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.safe_text));
        }

        if (ride.getPanicActivated()) {
            holder.tvSafety.setText("Panic");
            holder.tvSafety.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.panic_background)));
            holder.tvSafety.setTextColor(ContextCompat.getColor(context, R.color.panic_text));
        } else {
            holder.tvSafety.setText("Safe");
            holder.tvSafety.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.safe_background)));
            holder.tvSafety.setTextColor(ContextCompat.getColor(context, R.color.safe_text));
        }
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onRideClick(ride.getRideId());
        });
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView tvStart, tvEnd, tvDate, tvPrice, tvStatus, tvSafety;

        RideViewHolder(View itemView) {
            super(itemView);
            tvStart = itemView.findViewById(R.id.tvStartLocation);
            tvEnd = itemView.findViewById(R.id.tvEndLocation);
            tvDate = itemView.findViewById(R.id.tvDateTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvSafety = itemView.findViewById(R.id.tvSafety);
        }
    }
}