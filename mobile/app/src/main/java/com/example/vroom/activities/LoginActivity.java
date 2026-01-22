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

import com.example.vroom.DTOs.MessageResponseDTO;
import com.example.vroom.DTOs.auth.requests.ForgotPasswordRequestDTO;
import com.example.vroom.DTOs.auth.requests.LoginRequestDTO;
import com.example.vroom.DTOs.auth.responses.LoginResponseDTO;
import com.example.vroom.R;
import com.example.vroom.data.local.StorageManager;
import com.example.vroom.network.RetrofitClient;

import java.time.LocalDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {
    private EditText emailInput;
    private EditText passInput;
    private Button forgotPassBtn;
    private Button loginBtn;

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
        Long expires = StorageManager.getLong("expires", null);

        // check this for milis if correct
        if(token != null && System.currentTimeMillis() < expires){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void forgotPassReq(){
        try{
            String email = emailInput.getText().toString().trim();

            if (email.isEmpty())
                throw new Exception("Email is missing");

            ForgotPasswordRequestDTO req = new ForgotPasswordRequestDTO(email);
            RetrofitClient.getAuthService().forgotPassword(req).enqueue(new Callback<MessageResponseDTO>() {
                @Override
                public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                    if(response.isSuccessful() && response.body() != null){
                        Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                        startActivity(intent);

                        finish();
                    }else {
                        Toast.makeText(LoginActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        }catch(Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loginReq(){
        try{
            String email = emailInput.getText().toString().trim();
            String password = passInput.getText().toString().trim();

            if (email.isEmpty())
                throw new Exception("Email is missing");

            if (password.isEmpty())
                throw new Exception("Password is missing");

            LoginRequestDTO req = new LoginRequestDTO(email, password);

            RetrofitClient.getAuthService().login(req).enqueue(new Callback<LoginResponseDTO>() {
                @Override
                public void onResponse(Call<LoginResponseDTO> call, Response<LoginResponseDTO> response) {
                    if(response.isSuccessful() && response.body() != null){
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_LONG).show();

                        StorageManager.saveLong("user_id", response.body().getUserID());
                        StorageManager.saveData("user_type", response.body().getType());
                        StorageManager.saveData("jwt", response.body().getToken());
                        StorageManager.saveLong("expires", response.body().getExpires());

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }catch(Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}