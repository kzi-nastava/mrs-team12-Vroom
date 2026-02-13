package com.example.vroom.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import com.example.vroom.DTOs.auth.requests.ChangePasswordRequestDTO;
import com.example.vroom.DTOs.registeredUser.UpdateProfileRequestDTO;
import com.example.vroom.R;
import com.example.vroom.fragments.RideStatisticsFragment;
import com.example.vroom.viewmodels.ProfileViewModel;

public class ProfileActivity extends BaseActivity {
    private ProfileViewModel profileViewModel;

    private EditText firstNameInfo;
    private EditText lastNameInfo;
    private EditText addressInfo;
    private EditText phoneInfo;
    private EditText emailInfo;
    private boolean editMode = false;
    private Button btnReports;
    private Button profileChangeButton;
    private Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isDriver = false;

        if (isDriver) {
            setContentView(R.layout.activity_driver_profile);
        } else {
            setContentView(R.layout.activity_user_profile);
        }

        firstNameInfo = findViewById(R.id.FirstNameInfo);
        lastNameInfo = findViewById(R.id.LastNameInfo);
        addressInfo = findViewById(R.id.AddressInfo);
        phoneInfo = findViewById(R.id.PhoneNumberInfo);
        emailInfo = findViewById(R.id.EmailInfo);

        profileChangeButton = findViewById(R.id.profileChangeButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        btnReports = findViewById(R.id.btnReports);

        disableEditMode();
        setEditable(emailInfo, false);

        profileChangeButton.setOnClickListener(v -> {
            if (!editMode) {
                enableEditMode();
            } else {
                saveChanges();
            }
        });

        btnReports.setOnClickListener(v -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new RideStatisticsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        observeProfile();
        loadProfile();
    }

    private void showChangePasswordDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_change_password);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        EditText oldPasswordInput = dialog.findViewById(R.id.oldPasswordInput);
        EditText newPasswordInput = dialog.findViewById(R.id.newPasswordInput);
        EditText confirmPasswordInput = dialog.findViewById(R.id.confirmPasswordInput);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        Button changeButton = dialog.findViewById(R.id.changePasswordButton);

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        changeButton.setOnClickListener(v -> {
            String oldPassword = oldPasswordInput.getText().toString();
            String newPassword = newPasswordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO(
                    oldPassword,
                    newPassword,
                    confirmPassword
            );

            profileViewModel.changePassword(dto);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void setEditable(EditText et, boolean editable) {
        et.setEnabled(editable);
        et.setFocusable(editable);
        et.setFocusableInTouchMode(editable);
        et.setCursorVisible(editable);
    }

    private void enableEditMode() {
        editMode = true;

        setEditable(firstNameInfo, true);
        setEditable(lastNameInfo, true);
        setEditable(addressInfo, true);
        setEditable(phoneInfo, true);

        profileChangeButton.setText("Save");
    }

    private void disableEditMode() {
        editMode = false;

        setEditable(firstNameInfo, false);
        setEditable(lastNameInfo, false);
        setEditable(addressInfo, false);
        setEditable(phoneInfo, false);

        profileChangeButton.setText("Change");
    }

    private void saveChanges() {
        UpdateProfileRequestDTO dto = new UpdateProfileRequestDTO(
                firstNameInfo.getText().toString(),
                lastNameInfo.getText().toString(),
                addressInfo.getText().toString(),
                phoneInfo.getText().toString()
        );

        profileViewModel.updateProfile(dto);
    }

    private void loadProfile() {
        profileViewModel.loadProfile();
    }

    private void observeProfile() {
        profileViewModel.getProfile().observe(this, user -> {
            if (user != null) {
                firstNameInfo.setText(user.getFirstName());
                lastNameInfo.setText(user.getLastName());
                addressInfo.setText(user.getAddress());
                phoneInfo.setText(user.getPhoneNumber());
                emailInfo.setText(user.getEmail());
            }
        });

        profileViewModel.getError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });

        profileViewModel.getUpdateSuccess().observe(this, success -> {
            if (success == null) return;

            if (success) {
                disableEditMode();
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
            }
        });

        profileViewModel.getPasswordChangeSuccess().observe(this, success -> {
            if (success == null) return;

            if (success) {
                Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onProfileButtonClicked() {
        Toast.makeText(this, "You're already Here!", Toast.LENGTH_SHORT).show();
    }
}
