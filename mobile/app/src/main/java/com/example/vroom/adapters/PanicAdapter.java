package com.example.vroom.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vroom.DTOs.panic.responses.PanicNotificationResponseDTO;
import com.example.vroom.R;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanicAdapter extends RecyclerView.Adapter<PanicAdapter.PanicViewHolder> {

    private List<PanicNotificationResponseDTO> alerts;
    private final OnPanicClickListener listener;

    public interface OnPanicClickListener {
        void onMapClick(Long rideId);
        void onResolveClick(Long alertId, int position);
    }

    public PanicAdapter(List<PanicNotificationResponseDTO> alerts, OnPanicClickListener listener) {
        this.alerts = alerts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PanicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_panic_card, parent, false);
        return new PanicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PanicViewHolder holder, int position) {
        PanicNotificationResponseDTO alert = alerts.get(position);

        holder.rideIdLabel.setText("Ride ID: #" + alert.getRideID());
        holder.activatedByLabel.setText(alert.getActivatedBy());

        if (alert.getActivatedAt() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formattedDate = alert.getActivatedAt().format(formatter);
            holder.timeLabel.setText(formattedDate);
        }

        holder.btnMap.setOnClickListener(v -> listener.onMapClick(alert.getRideID()));
        holder.btnResolve.setOnClickListener(v -> listener.onResolveClick(alert.getId(), holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return alerts != null ? alerts.size() : 0;
    }

    public void updateData(List<PanicNotificationResponseDTO> newAlerts) {
        this.alerts = newAlerts;
        notifyDataSetChanged();
    }

    public static class PanicViewHolder extends RecyclerView.ViewHolder {
        TextView rideIdLabel, activatedByLabel, timeLabel;
        Button btnMap, btnResolve;

        public PanicViewHolder(@NonNull View itemView) {
            super(itemView);

            rideIdLabel = itemView.findViewById(R.id.ride_id_label);
            activatedByLabel = itemView.findViewById(R.id.activated_by_label);
            timeLabel = itemView.findViewById(R.id.time_label);
            btnMap = itemView.findViewById(R.id.btn_redirect_map);
            btnResolve = itemView.findViewById(R.id.btn_resolve);
        }
    }
}
