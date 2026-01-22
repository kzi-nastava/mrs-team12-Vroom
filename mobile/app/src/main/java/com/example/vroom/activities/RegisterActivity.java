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
import androidx.lifecycle.ViewModelProvider;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.auth.requests.RegisterUserRequestDTO;
import com.example.vroom.R;
import com.example.vroom.enums.Gender;
import com.example.vroom.network.RetrofitClient;
import com.example.vroom.utils.ImageUtils;
import com.example.vroom.utils.PasswordUtils;
import com.example.vroom.viewmodels.ForgotPasswordViewModel;
import com.example.vroom.viewmodels.RegisterViewModel;

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

    private RegisterViewModel viewModel;

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

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        observeViewModel();
    }

    private void observeViewModel(){
        viewModel.getRegisterMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getRegisterStatus().observe(this, success -> {
            if (success != null && success) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
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

    private void register(){
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneNumInput.getText().toString().trim();
        String country = countrySpinner.getSelectedItem().toString();
        String city = cityInput.getText().toString().trim();
        String street = streetInput.getText().toString().trim();
        String pass = passInput.getText().toString();
        String rePass = rePassInput.getText().toString();

        byte[] photo = ImageUtils.uriToByteArray(this, selectedImage);

        viewModel.register(firstName, lastName, email, phone, country, city, street, pass, rePass, selectedGender, photo);
    }
}