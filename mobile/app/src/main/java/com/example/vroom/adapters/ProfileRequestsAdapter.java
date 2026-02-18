package com.example.vroom.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vroom.DTOs.admin.DriverUpdateRequestAdminDTO;
import com.example.vroom.DTOs.driver.requests.DriverDTO;
import com.example.vroom.R;


import java.util.ArrayList;
import java.util.List;

public class ProfileRequestsAdapter extends RecyclerView.Adapter<ProfileRequestsAdapter.ViewHolder> {

    private List<DriverUpdateRequestAdminDTO> requests = new ArrayList<>();
    private final OnApproveListener onApproveListener;
    private final OnRejectListener onRejectListener;

    public interface OnApproveListener {
        void onApprove(Long requestId);
    }

    public interface OnRejectListener {
        void onReject(Long requestId, String comment);
    }

    public ProfileRequestsAdapter(OnApproveListener onApproveListener, OnRejectListener onRejectListener) {
        this.onApproveListener = onApproveListener;
        this.onRejectListener = onRejectListener;
    }

    public void setRequests(List<DriverUpdateRequestAdminDTO> requests) {
        this.requests = requests;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DriverUpdateRequestAdminDTO request = requests.get(position);
        holder.bind(request);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView emailText;
        TextView phoneGenderText;
        TextView vehicleText;
        Button approveButton;
        Button rejectButton;
        EditText rejectComment;

        ViewHolder(View itemView) {
            super(itemView);
            emailText = itemView.findViewById(R.id.request_email);
            phoneGenderText = itemView.findViewById(R.id.request_phone_gender);
            vehicleText = itemView.findViewById(R.id.request_vehicle);
            approveButton = itemView.findViewById(R.id.approve_button);
            rejectButton = itemView.findViewById(R.id.reject_button);
            rejectComment = itemView.findViewById(R.id.reject_comment);
        }

        void bind(DriverUpdateRequestAdminDTO request) {
            DriverDTO payload = request.getPayload();

            emailText.setText(payload.getEmail());

            String phoneGender = payload.getPhoneNumber() + " / " + payload.getGender();
            phoneGenderText.setText(phoneGender);

            if (payload.getVehicle() != null) {
                String vehicleInfo = payload.getVehicle().getBrand() + " " +
                        payload.getVehicle().getModel() + " (" +
                        payload.getVehicle().getLicenceNumber() + ")";
                vehicleText.setText(vehicleInfo);
            } else {
                vehicleText.setText("No vehicle info");
            }


            approveButton.setOnClickListener(v -> {
                if (onApproveListener != null) {
                    onApproveListener.onApprove(request.getId());
                }
            });


            rejectButton.setOnClickListener(v -> {
                String comment = rejectComment.getText().toString().trim();

                if (comment.isEmpty()) {
                    rejectComment.setError("Comment is required");
                    rejectComment.requestFocus();
                    Toast.makeText(itemView.getContext(),
                            "Please provide a reject reason", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (comment.length() < 10) {
                    rejectComment.setError("Comment must be at least 10 characters");
                    rejectComment.requestFocus();
                    Toast.makeText(itemView.getContext(),
                            "Comment is too short", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (onRejectListener != null) {
                    onRejectListener.onReject(request.getId(), comment);
                    rejectComment.setText("");
                }
            });
        }
    }
}