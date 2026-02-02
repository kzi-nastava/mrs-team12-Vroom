package com.example.vroom.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.vroom.DTOs.driver.requests.DriverDTO;
import com.example.vroom.R;
import com.example.vroom.viewmodels.DriverProfileViewModel;


public class DriverProfileActivity extends BaseActivity {

    private DriverProfileViewModel viewModel;

    private EditText firstNameInfo, lastNameInfo, emailInfo,
            addressInfo, phoneInfo;

    private TextView brandInfo, modelInfo, seatsInfo,
            licensePlateInfo, babiesInfo, petsInfo;

    private boolean editMode = false;
    private Button profileChangeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        bindViews();

        viewModel = new ViewModelProvider(this).get(DriverProfileViewModel.class);

        observe();

        setEditable(false);
        disableEditMode();

        profileChangeButton.setOnClickListener(v -> {
            if (!editMode) {
                enableEditMode();
            } else {
                saveChanges();
                disableEditMode();
            }
        });

        viewModel.loadProfile();
    }

    private void bindViews() {
        firstNameInfo = findViewById(R.id.FirstNameInfo);
        lastNameInfo = findViewById(R.id.LastNameInfo);
        emailInfo = findViewById(R.id.EmailInfo);
        addressInfo = findViewById(R.id.AddressInfo);
        phoneInfo = findViewById(R.id.PhoneNumberInfo);

        brandInfo = findViewById(R.id.BrandInfo);
        modelInfo = findViewById(R.id.ModelInfo);
        seatsInfo = findViewById(R.id.NumberOfSeatsInfo);
        licensePlateInfo = findViewById(R.id.LicensePlateInfo);
        babiesInfo = findViewById(R.id.BabiesInfo);
        petsInfo = findViewById(R.id.PetsInfo);

        profileChangeButton = findViewById(R.id.profileChangeButton);
    }

    private void observe() {
        viewModel.getProfile().observe(this, driver -> {
            if (driver == null) return;

            firstNameInfo.setText(driver.getFirstName());
            lastNameInfo.setText(driver.getLastName());
            emailInfo.setText(driver.getEmail());
            addressInfo.setText(driver.getAddress());
            phoneInfo.setText(driver.getPhoneNumber());

            DriverDTO.VehicleDTO vehicle = driver.getVehicle();
            if (vehicle != null) {
                brandInfo.setText(vehicle.getBrand());
                modelInfo.setText(vehicle.getModel());
                seatsInfo.setText(
                        vehicle.getNumberOfSeats() != null
                                ? String.valueOf(vehicle.getNumberOfSeats())
                                : ""
                );
                licensePlateInfo.setText(vehicle.getLicenceNumber());
                babiesInfo.setText(
                        vehicle.getBabiesAllowed() != null && vehicle.getBabiesAllowed()
                                ? "Yes" : "No"
                );
                petsInfo.setText(
                        vehicle.getPetsAllowed() != null && vehicle.getPetsAllowed()
                                ? "Yes" : "No"
                );
            }
        });

        viewModel.getError().observe(this, err ->
                Toast.makeText(this, err, Toast.LENGTH_LONG).show()
        );

        viewModel.getUpdateSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(
                        this,
                        "Request sent. Waiting for admin approval.",
                        Toast.LENGTH_LONG
                ).show();
                viewModel.loadProfile();
            }
        });
    }

    private void setEditable(boolean enabled) {
        firstNameInfo.setEnabled(enabled);
        lastNameInfo.setEnabled(enabled);
        addressInfo.setEnabled(enabled);
        phoneInfo.setEnabled(enabled);

        brandInfo.setEnabled(enabled);
        modelInfo.setEnabled(enabled);
        seatsInfo.setEnabled(enabled);
        licensePlateInfo.setEnabled(enabled);

        babiesInfo.setEnabled(false);
        petsInfo.setEnabled(false);
        emailInfo.setEnabled(false);
    }

    private void enableEditMode() {
        editMode = true;
        setEditable(true);
        profileChangeButton.setText("Save");
    }

    private void disableEditMode() {
        editMode = false;
        setEditable(false);
        profileChangeButton.setText("Change");
    }

    private void saveChanges() {

        DriverDTO existing = viewModel.getProfile().getValue();
        if (existing == null) return;

        DriverDTO dto = new DriverDTO();

        dto.setEmail(existing.getEmail());
        dto.setGender(existing.getGender());
        dto.setStatus(existing.getStatus());
        dto.setRatingCount(existing.getRatingCount());
        dto.setRatingSum(existing.getRatingSum());

        dto.setFirstName(firstNameInfo.getText().toString());
        dto.setLastName(lastNameInfo.getText().toString());
        dto.setAddress(addressInfo.getText().toString());
        dto.setPhoneNumber(phoneInfo.getText().toString());

        dto.setVehicle(existing.getVehicle());

        viewModel.requestProfileUpdate(dto);
    }
}
