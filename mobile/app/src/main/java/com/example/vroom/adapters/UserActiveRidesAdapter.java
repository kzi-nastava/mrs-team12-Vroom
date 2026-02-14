package com.example.vroom.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vroom.DTOs.ride.responses.UserActiveRideDTO;
import com.example.vroom.R;
import java.time.format.DateTimeFormatter;

public class UserActiveRidesAdapter extends ListAdapter<UserActiveRideDTO, UserActiveRidesAdapter.ViewHolder> {
    private final OnRideClickListener listener;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public interface OnRideClickListener {
        void onTrackRide(Long rideId);
        void onCancelRide(Long rideId);
    }

    public UserActiveRidesAdapter(OnRideClickListener listener) {
        super(new DiffUtil.ItemCallback<UserActiveRideDTO>() {
            @Override
            public boolean areItemsTheSame(@NonNull UserActiveRideDTO oldItem, @NonNull UserActiveRideDTO newItem) {
                return oldItem.getRideID().equals(newItem.getRideID());
            }
            @Override
            public boolean areContentsTheSame(@NonNull UserActiveRideDTO oldItem, @NonNull UserActiveRideDTO newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_active_ride, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserActiveRideDTO ride = getItem(position);

        holder.rideId.setText("Ride #" + ride.getRideID());
        holder.price.setText(String.format("%.2f RSD", ride.getPrice()));
        holder.startAddress.setText(ride.getRoute().getStartAddress());
        holder.endAddress.setText(ride.getRoute().getEndAddress());
        holder.driverName.setText(ride.getDriverName());
        holder.vehicleInfo.setText(ride.getVehicleInfo());
        holder.statusTag.setText(ride.getStatus().toString());

        if (ride.getScheduledTime() != null) {
            String label = ride.isScheduled() ? "Scheduled: " : "Requested: ";
            holder.timeHeader.setText(label + ride.getScheduledTime().format(timeFormatter));
            holder.timeHeader.setVisibility(View.VISIBLE);
        } else {
            holder.timeHeader.setVisibility(View.GONE);
        }

        String status = ride.getStatus().toString();
        holder.btnCancel.setVisibility((status.equals("ACCEPTED") || status.equals("PENDING")) ? View.VISIBLE : View.GONE);
        holder.btnTrack.setVisibility(status.equals("ONGOING") ? View.VISIBLE : View.GONE);

        holder.btnTrack.setOnClickListener(v -> listener.onTrackRide(ride.getRideID()));
        holder.btnCancel.setOnClickListener(v -> listener.onCancelRide(ride.getRideID()));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rideId, timeHeader, price, startAddress, endAddress, driverName, vehicleInfo, statusTag;
        Button btnTrack, btnCancel;

        ViewHolder(View v) {
            super(v);
            rideId = v.findViewById(R.id.text_ride_id);
            timeHeader = v.findViewById(R.id.text_time_header);
            price = v.findViewById(R.id.text_price);
            startAddress = v.findViewById(R.id.text_start_address);
            endAddress = v.findViewById(R.id.text_end_address);
            driverName = v.findViewById(R.id.text_driver_name);
            vehicleInfo = v.findViewById(R.id.text_vehicle_info);
            statusTag = v.findViewById(R.id.text_status_tag);
            btnTrack = v.findViewById(R.id.btn_track_ride);
            btnCancel = v.findViewById(R.id.btn_cancel_ride);
        }
    }
}