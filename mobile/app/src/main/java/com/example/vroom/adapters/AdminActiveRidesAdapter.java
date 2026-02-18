package com.example.vroom.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vroom.DTOs.ride.responses.GetActiveRideInfoDTO;
import com.example.vroom.R;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Formatter;
import java.util.List;

public class AdminActiveRidesAdapter extends RecyclerView.Adapter<AdminActiveRidesAdapter.ViewHolder>{
    private List<GetActiveRideInfoDTO> rides;
    private final OnRideClickListener listener;

    public interface OnRideClickListener {
        void onRideClick(Long rideId);
    }

    public AdminActiveRidesAdapter(List<GetActiveRideInfoDTO> rides, OnRideClickListener listener) {
        this.rides = rides;
        this.listener = listener;
    }

    public void updateData(List<GetActiveRideInfoDTO> newRides) {
        this.rides = newRides;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ride_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GetActiveRideInfoDTO ride = rides.get(position);
        holder.txtDriver.setText(ride.getDriverName());
        holder.txtRoute.setText(ride.getStartAddress() + " → " + ride.getEndAddress());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm · dd.MM.yyyy.");
        holder.txtTime.setText("Start Time: " + ride.getStartTime().format(formatter));
        holder.txtPassenger.setText("Passenger: " + ride.getCreatorName());

        if (ride.getProfilePicture() != null) {
            byte[] decodedString = Base64.decode(ride.getProfilePicture(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.imgProfile.setImageBitmap(decodedByte);
        } else {
            holder.imgProfile.setImageResource(R.drawable.ic_user);
        }

        holder.itemView.setOnClickListener(v -> listener.onRideClick(ride.getRideId()));
    }

    @Override
    public int getItemCount() { return rides.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDriver, txtRoute, txtTime, txtPassenger;
        ImageView imgProfile;

        ViewHolder(View itemView) {
            super(itemView);
            txtDriver = itemView.findViewById(R.id.txtDriverName);
            txtRoute = itemView.findViewById(R.id.txtRoute);
            txtTime = itemView.findViewById(R.id.txtStartTime);
            txtPassenger = itemView.findViewById(R.id.txtPassenger);
            imgProfile = itemView.findViewById(R.id.imgProfile);
        }
    }
}
