package com.example.vroom.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vroom.DTOs.ride.requests.DailyRideReportDTO;
import com.example.vroom.R;


import java.time.format.DateTimeFormatter;
import java.util.List;

public class DailyStatsAdapter extends RecyclerView.Adapter<DailyStatsAdapter.DailyStatViewHolder> {

    private List<DailyRideReportDTO> dailyStats;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DailyStatsAdapter(List<DailyRideReportDTO> dailyStats) {
        this.dailyStats = dailyStats;
    }

    @NonNull
    @Override
    public DailyStatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily_stat, parent, false);
        return new DailyStatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyStatViewHolder holder, int position) {
        DailyRideReportDTO stat = dailyStats.get(position);
        holder.bind(stat, dateFormatter);
    }

    @Override
    public int getItemCount() {
        return dailyStats.size();
    }

    public void updateStats(List<DailyRideReportDTO> newStats) {
        this.dailyStats = newStats;
        notifyDataSetChanged();
    }

    static class DailyStatViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvRides, tvMoney, tvDistance;

        public DailyStatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvRides = itemView.findViewById(R.id.tv_rides);
            tvMoney = itemView.findViewById(R.id.tv_money);
            tvDistance = itemView.findViewById(R.id.tv_distance);
        }

        public void bind(DailyRideReportDTO stat, DateTimeFormatter dateFormatter) {
            tvDate.setText(stat.getDate().format(dateFormatter));
            tvRides.setText(String.valueOf(stat.getRideCount()));
            tvMoney.setText(String.format("$%.2f", stat.getMoney()));
            tvDistance.setText(String.format("%.2f km", stat.getKm()));
        }
    }
}
