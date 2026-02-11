package com.example.vroom.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.vroom.DTOs.admin.AdminUserDTO;
import com.example.vroom.R;
import com.example.vroom.viewmodels.AdminViewModel;

public class BlockUserFragment extends Fragment {

    private AdminViewModel viewModel;
    private LinearLayout userContainer;
    private ProgressBar progressBar;
    private TextView emptyView;

    public BlockUserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_block_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userContainer = view.findViewById(R.id.user_container);
        progressBar = new ProgressBar(getContext());
        emptyView = new TextView(getContext());
        emptyView.setText("No users found");
        emptyView.setTextSize(16);
        emptyView.setPadding(16, 16, 16, 16);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        observeViewModel();
        viewModel.loadUsers();
    }

    private void observeViewModel() {
        viewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            if (users != null && !users.isEmpty()) {
                displayUsers(users);
            } else {
                userContainer.removeAllViews();
                userContainer.addView(emptyView);
            }
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
            if (loading != null && loading) {
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), success -> {
            if (success != null && !success.isEmpty()) {
                Toast.makeText(getContext(), success, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayUsers(java.util.List<AdminUserDTO> users) {
        userContainer.removeAllViews();

        for (AdminUserDTO user : users) {
            View userCard = createUserCard(user);
            userContainer.addView(userCard);
        }
    }

    private View createUserCard(AdminUserDTO user) {
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_card, userContainer, false);

        TextView userEmail = cardView.findViewById(R.id.user_email);
        TextView userStatus = cardView.findViewById(R.id.user_status);
        EditText blockReasonInput = cardView.findViewById(R.id.block_reason_input);
        Button blockButton = cardView.findViewById(R.id.block_button);
        Button unblockButton = cardView.findViewById(R.id.unblock_button);


        userEmail.setText(user.getEmail());


        boolean isBlocked = user.getBlocked() != null ? user.getBlocked() : false;

        if (isBlocked) {
            userStatus.setText("Blocked");
            userStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
            blockButton.setVisibility(View.GONE);
            unblockButton.setVisibility(View.VISIBLE);
            blockReasonInput.setEnabled(false);
        } else {
            userStatus.setText("Active");
            userStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));
            blockButton.setVisibility(View.VISIBLE);
            unblockButton.setVisibility(View.GONE);
            blockReasonInput.setEnabled(true);
        }


        blockButton.setOnClickListener(v -> {
            String reason = blockReasonInput.getText().toString().trim();

            if (reason.isEmpty()) {
                blockReasonInput.setError("Reason is required");
                blockReasonInput.requestFocus();
                Toast.makeText(getContext(), "Please provide a reason for blocking", Toast.LENGTH_SHORT).show();
                return;
            }

            if (reason.length() < 10) {
                blockReasonInput.setError("Reason must be at least 10 characters");
                blockReasonInput.requestFocus();
                Toast.makeText(getContext(), "Reason is too short", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.blockUser(user.getId(), reason);
        });

        unblockButton.setOnClickListener(v -> {
            viewModel.unblockUser(user.getId());
        });

        return cardView;
    }
}
