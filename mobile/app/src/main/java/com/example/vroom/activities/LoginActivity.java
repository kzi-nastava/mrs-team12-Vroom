package com.example.vroom.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.auth.requests.ForgotPasswordRequestDTO;
import com.example.vroom.DTOs.auth.requests.LoginRequestDTO;
import com.example.vroom.DTOs.auth.responses.LoginResponseDTO;
import com.example.vroom.R;
import com.example.vroom.data.local.StorageManager;
import com.example.vroom.network.RetrofitClient;
import com.example.vroom.viewmodels.LoginViewModel;
import com.example.vroom.viewmodels.NavigationViewModel;

import java.time.LocalDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {
    private EditText emailInput;
    private EditText passInput;
    private Button forgotPassBtn;
    private Button loginBtn;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailInput = findViewById(R.id.emailInput);
        passInput = findViewById(R.id.passwordInput);

        forgotPassBtn = findViewById(R.id.forgotPasswordButton);
        forgotPassBtn.setOnClickListener(v -> this.forgotPassReq());

        loginBtn = findViewById(R.id.loginButton);
        loginBtn.setOnClickListener(v -> this.loginReq());

        StorageManager.getSharedPreferences(this);

        String token = StorageManager.getData("jwt", null);
        long expires = StorageManager.getLong("expires", -1L);

        // check this for milis if correct
        if(token != null && System.currentTimeMillis() < expires){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        observeViewModel();
    }
    private void observeViewModel(){
        viewModel.getLoginMessage().observe(this, message -> {
            showToast(message);
        });

        viewModel.getLoginStatus().observe(this, success -> {
            if (success != null && success) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        viewModel.getForgotPasswordMessage().observe(this, message -> {
            showToast(message);
        });

        viewModel.getForgotPasswordStatus().observe(this, success -> {
            if (success != null && success) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showToast(String message){
        if(message != null && !message.isEmpty()){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
    private void forgotPassReq(){
        String email = emailInput.getText().toString().trim();

        viewModel.forgotPassword(email);
    }

    private void loginReq(){
        String email = emailInput.getText().toString().trim();
        String password = passInput.getText().toString().trim();

        viewModel.login(email, password);
    }

}