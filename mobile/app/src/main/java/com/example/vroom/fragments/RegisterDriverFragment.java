package com.example.vroom.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.vroom.R;
import com.example.vroom.enums.VehicleType;
import com.example.vroom.viewmodels.AdminViewModel;

public class RegisterDriverFragment extends Fragment {

    private EditText firstName, lastName, email, phoneNumber;
    private EditText country, city, street;
    private RadioGroup genderGroup;
    private EditText vehicleModel, vehicleBrand, plateNumber, numberOfSeats;
    private Spinner vehicleType;
    private CheckBox petsAllowed, babiesAllowed;
    private Button registerBtn;
    private AdminViewModel viewModel;

    public RegisterDriverFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_driver, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupSpinner();

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        observeViewModel();

        registerBtn.setOnClickListener(v -> registerDriver());
    }

    private void initViews(View view) {
        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        email = view.findViewById(R.id.email);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        country = view.findViewById(R.id.country);
        city = view.findViewById(R.id.city);
        street = view.findViewById(R.id.street);
        genderGroup = view.findViewById(R.id.genderGroup);
        vehicleModel = view.findViewById(R.id.vehicleModel);
        vehicleBrand = view.findViewById(R.id.vehicleBrand);
        plateNumber = view.findViewById(R.id.plateNumber);
        numberOfSeats = view.findViewById(R.id.numberOfSeats);
        vehicleType = view.findViewById(R.id.vehicleType);
        petsAllowed = view.findViewById(R.id.petsAllowed);
        babiesAllowed = view.findViewById(R.id.babiesAllowed);
        registerBtn = view.findViewById(R.id.registerBtn);
    }

    private void setupSpinner() {
        ArrayAdapter<VehicleType> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                VehicleType.values()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleType.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), success -> {
            if (success != null && !success.isEmpty()) {
                Toast.makeText(getContext(), success, Toast.LENGTH_LONG).show();
                clearForm();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
            registerBtn.setEnabled(!loading);
            registerBtn.setText(loading ? "Registering..." : "Register Driver");
        });
    }

    private void registerDriver() {
        String fName = firstName.getText().toString().trim();
        String lName = lastName.getText().toString().trim();
        String mail = email.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();
        String countryText = country.getText().toString().trim();
        String cityText = city.getText().toString().trim();
        String streetText = street.getText().toString().trim();

        String gender = getSelectedGender();


        String model = vehicleModel.getText().toString().trim();
        String brand = vehicleBrand.getText().toString().trim();
        String plate = plateNumber.getText().toString().trim();
        String seats = numberOfSeats.getText().toString().trim();
        VehicleType type = (VehicleType) vehicleType.getSelectedItem();
        boolean pets = petsAllowed.isChecked();
        boolean babies = babiesAllowed.isChecked();


        if (fName.isEmpty() || lName.isEmpty() || mail.isEmpty() ||
                phone.isEmpty() || countryText.isEmpty() || cityText.isEmpty() ||
                streetText.isEmpty() || gender == null) {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            email.setError("Invalid email format");
            return;
        }

        boolean hasAnyVehicleData = !model.isEmpty() || !brand.isEmpty() ||
                !plate.isEmpty() || !seats.isEmpty();

        if (hasAnyVehicleData) {
            if (model.isEmpty() || brand.isEmpty() || plate.isEmpty() || seats.isEmpty()) {
                Toast.makeText(getContext(),
                        "If you enter vehicle data, all vehicle fields are required",
                        Toast.LENGTH_LONG).show();
                return;
            }

            try {
                int seatsCount = Integer.parseInt(seats);
                if (seatsCount <= 0) {
                    numberOfSeats.setError("Must be positive number");
                    return;
                }
            } catch (NumberFormatException e) {
                numberOfSeats.setError("Invalid number");
                return;
            }
        }

        String address = streetText + ", " + cityText + ", " + countryText;

        Integer seatsInt = null;
        if (!seats.isEmpty()) {
            seatsInt = Integer.parseInt(seats);
        }

        viewModel.registerDriver(
                fName, lName, mail, phone, address, gender,
                brand.isEmpty() ? null : brand,
                model.isEmpty() ? null : model,
                type,
                plate.isEmpty() ? null : plate,
                seatsInt,
                hasAnyVehicleData ? babies : null,
                hasAnyVehicleData ? pets : null
        );
    }

    private String getSelectedGender() {
        int selectedId = genderGroup.getCheckedRadioButtonId();
        if (selectedId == -1) return null;

        RadioButton selected = getView().findViewById(selectedId);
        return selected.getText().toString().toUpperCase();
    }

    private void clearForm() {
        firstName.setText("");
        lastName.setText("");
        email.setText("");
        phoneNumber.setText("");
        country.setText("");
        city.setText("");
        street.setText("");
        genderGroup.clearCheck();
        vehicleModel.setText("");
        vehicleBrand.setText("");
        plateNumber.setText("");
        numberOfSeats.setText("");
        vehicleType.setSelection(0);
        petsAllowed.setChecked(false);
        babiesAllowed.setChecked(false);
    }
}
