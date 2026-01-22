package com.example.vroom.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vroom.DTOs.MessageResponse;
import com.example.vroom.DTOs.auth.requests.RegisterUserRequestDTO;
import com.example.vroom.R;
import com.example.vroom.enums.Gender;
import com.example.vroom.network.RetrofitClient;
import com.example.vroom.utils.ImageUtils;
import com.example.vroom.utils.PasswordUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity {
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private EditText phoneNumInput;
    private Spinner countrySpinner;
    private EditText cityInput;
    private EditText streetInput;
    private RadioGroup genderRadioGroup;
    private String selectedGender;
    private EditText passInput;
    private EditText rePassInput;
    private Button registerBtn;

    private ImageView profileImageView;
    private Button uploadImageBtn;
    private Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneNumInput = findViewById(R.id.phoneNumInput);

        countrySpinner = findViewById(R.id.countrySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.countries_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        cityInput = findViewById(R.id.cityInput);
        streetInput = findViewById(R.id.streetInput);

        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        genderRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selected = findViewById(checkedId);
            if (selected != null) {
                selectedGender = selected.getText().toString();
            }
        });

        passInput = findViewById(R.id.passwordInput);
        rePassInput = findViewById(R.id.confirmPasswordInput);

        registerBtn = findViewById(R.id.registerButton);
        registerBtn.setOnClickListener(v -> register());

        profileImageView = findViewById(R.id.profileImage);
        uploadImageBtn = findViewById(R.id.uploadImageBtn);
        uploadImageBtn.setOnClickListener(v -> getContent.launch("image/*"));
    }

    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImage = uri;
                    profileImageView.setImageURI(uri);
                }
            }
    );

    private Gender getGender(){
        if(selectedGender.equalsIgnoreCase("male"))
            return Gender.MALE;
        else if (selectedGender.equalsIgnoreCase("FEMALE"))
            return Gender.FEMALE;
        else
            return Gender.OTHER;
    }

    private void register(){
        try{
            String firstName = firstNameInput.getText().toString().trim();
            String lastName = lastNameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String phone = phoneNumInput.getText().toString().trim();
            String country = countrySpinner.getSelectedItem().toString();
            String city = cityInput.getText().toString().trim();
            String street = streetInput.getText().toString().trim();
            String pass = passInput.getText().toString();
            String rePass = rePassInput.getText().toString();

            if(
                    firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                    phone.isEmpty() || country.isEmpty() || city.isEmpty() ||
                    street.isEmpty() || pass.isEmpty() || rePass.isEmpty() ||
                    selectedGender==null
            )
                throw new Exception("Fields cannot be empty");

            PasswordUtils.isPasswordValid(passInput.getText().toString(), rePassInput.getText().toString());

            String address = street + ", " + city + ", " + country;
            Gender genderEnum = getGender();

            byte[] photo = ImageUtils.uriToByteArray(this, selectedImage);

            RegisterUserRequestDTO req = new RegisterUserRequestDTO(firstName, lastName, email, phone,
                    address, genderEnum, photo, pass);

            RetrofitClient.getAuthService().registerUser(req).enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    if(response.isSuccessful() && response.body() != null){
                        Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);

                        finish();

                    }else
                        Toast.makeText(RegisterActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }catch(Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}