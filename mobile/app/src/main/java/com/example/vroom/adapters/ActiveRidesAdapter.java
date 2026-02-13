package com.example.vroom.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.vroom.DTOs.ride.responses.GetRideResponseDTO;
import com.example.vroom.R;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ActiveRidesAdapter extends RecyclerView.Adapter<ActiveRidesAdapter.RideViewHolder> {

    private List<GetRideResponseDTO> rides;
    private OnRideActionListener listener;

    public interface OnRideActionListener {
        void onStartRide(GetRideResponseDTO ride);
        void onCancelRide(GetRideResponseDTO ride);
    }

    public ActiveRidesAdapter(List<GetRideResponseDTO> rides, OnRideActionListener listener) {
        this.rides = rides;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ride, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        GetRideResponseDTO ride = rides.get(position);
        holder.bind(ride, listener);
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    public void updateRides(List<GetRideResponseDTO> newRides) {
        this.rides = newRides;
        notifyDataSetChanged();
    }

    static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvStatus, tvRoute, tvStartTime, tvPrice, tvPassengerCount;
        TextView tvVehicle, tvPassengerNames;
        Button btnStartRide, btnCancelRide;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvPassengerCount = itemView.findViewById(R.id.tvPassengerCount);
            tvVehicle = itemView.findViewById(R.id.tvVehicle);
            tvPassengerNames = itemView.findViewById(R.id.tvPassengerNames);
            btnStartRide = itemView.findViewById(R.id.btnStartRide);
            btnCancelRide = itemView.findViewById(R.id.btnCancelRide);
        }

        public void bind(GetRideResponseDTO ride, OnRideActionListener listener) {
            tvStatus.setText(ride.getStatus().toString());

            if (ride.getRoute() != null) {
                String startAddr = ride.getRoute().getStartAddress() != null ?
                        ride.getRoute().getStartAddress() : "Start";
                String endAddr = ride.getRoute().getEndAddress() != null ?
                        ride.getRoute().getEndAddress() : "End";

                String route = startAddr + " → " + endAddr;
                tvRoute.setText(route);
            } else {
                tvRoute.setText("-");
            }

            if (ride.getStartTime() != null) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    tvStartTime.setText(ride.getStartTime().format(formatter));
                } catch (Exception e) {
                    tvStartTime.setText("-");
                }
            } else {
                tvStartTime.setText("-");
            }

            tvPrice.setText(String.format("%.2f $", ride.getPrice()));

            int passengerCount = ride.getPassengers() != null ? ride.getPassengers().size() : 0;
            tvPassengerCount.setText(String.valueOf(passengerCount));

            if (ride.getDriver() != null && ride.getDriver().getVehicle() != null) {
                String vehicleInfo = "";

                if (ride.getDriver().getVehicle().getBrand() != null) {
                    vehicleInfo = ride.getDriver().getVehicle().getBrand() + " ";
                }

                if (ride.getDriver().getVehicle().getModel() != null) {
                    vehicleInfo += ride.getDriver().getVehicle().getModel();
                }

                if (ride.getDriver().getVehicle().getLicensePlate() != null) {
                    vehicleInfo += " (" + ride.getDriver().getVehicle().getLicensePlate() + ")";
                }

                tvVehicle.setText(vehicleInfo.trim().isEmpty() ? "-" : vehicleInfo);
            } else {
                tvVehicle.setText("-");
            }

            if (ride.getPassengers() != null && !ride.getPassengers().isEmpty()) {
                String passengerNames = String.join("\n", ride.getPassengers());
                tvPassengerNames.setText(passengerNames);
            } else {
                tvPassengerNames.setText("-");
            }

            switch (ride.getStatus()) {
                case ACCEPTED:
                    btnStartRide.setVisibility(View.VISIBLE);
                    btnStartRide.setEnabled(true);
                    btnCancelRide.setVisibility(View.VISIBLE);
                    tvTitle.setText("Active Ride");
                    break;
                case ONGOING:
                    btnStartRide.setVisibility(View.GONE);
                    btnCancelRide.setVisibility(View.GONE);
                    tvTitle.setText("Ongoing Ride");
                    break;
                default:
                    btnStartRide.setVisibility(View.VISIBLE);
                    btnStartRide.setEnabled(false);
                    btnCancelRide.setVisibility(View.VISIBLE);
                    tvTitle.setText("Active Ride");
                    break;
            }
            btnStartRide.setOnClickListener(v -> listener.onStartRide(ride));
            btnCancelRide.setOnClickListener(v -> listener.onCancelRide(ride));
        }
    }
}