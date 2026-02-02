package com.example.vroom.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.Observer;
import android.widget.TextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import androidx.appcompat.app.AppCompatActivity;

import com.example.vroom.DTOs.registeredUser.UpdateProfileRequestDTO;
import com.example.vroom.R;
import com.example.vroom.network.RetrofitClient;
import com.example.vroom.viewmodels.ProfileViewModel;

public class ProfileActivity extends BaseActivity {
    private ProfileViewModel profileViewModel;

    private EditText firstNameInfo;
    private EditText lastNameInfo;
    private EditText addressInfo;
    private EditText phoneInfo;
    private EditText emailInfo;
    private boolean editMode = false;


    private Button profileChangeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isDriver = false; // kasnije vučeš iz tokena / user role

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
        disableEditMode();
        setEditable(emailInfo, false);


        profileChangeButton.setOnClickListener(v -> {
            if (!editMode) {
                enableEditMode();
            } else {
                saveChanges();
            }
        });


        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        observeProfile();
        loadProfile();
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
        } );

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
    }
    @Override
    public void onProfileButtonClicked(){
        Toast.makeText(this, "You're already Here !", Toast.LENGTH_SHORT).show();
    }

}
