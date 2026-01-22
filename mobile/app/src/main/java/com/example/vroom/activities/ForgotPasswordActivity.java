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
import com.example.vroom.DTOs.auth.requests.ResetPasswordRequestDTO;
import com.example.vroom.R;
import com.example.vroom.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends BaseActivity {
    private EditText emailInput;
    private EditText codeInput;
    private EditText passInput;
    private EditText rePassInput;
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailInput = findViewById(R.id.emailInput);
        codeInput = findViewById(R.id.codeInput);
        passInput = findViewById(R.id.passwordInput);
        rePassInput = findViewById(R.id.confirmPasswordInput);

        submitBtn = findViewById(R.id.submitButton);

        submitBtn.setOnClickListener(v -> resetPass());
    }

    private void resetPass(){
        try{
            String email = emailInput.getText().toString().trim();
            String code = codeInput.getText().toString().trim();
            String pass = passInput.getText().toString().trim();
            String rePass = rePassInput.getText().toString().trim();

            if (email.isEmpty() || code.isEmpty() || pass.isEmpty() || rePass.isEmpty())
                throw new Exception("Fields are missing");

            if(!pass.equals(rePass))
                throw new Exception("Password must match");


            ResetPasswordRequestDTO req = new ResetPasswordRequestDTO(email, code, pass);
            RetrofitClient.getAuthService().resetPassword(req).enqueue(new Callback<MessageResponseDTO>() {
                @Override
                public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                    if(response.isSuccessful() && response.body() != null){
                        Toast.makeText(ForgotPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);

                        finish();
                    }else {
                        Toast.makeText(ForgotPasswordActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                    Toast.makeText(ForgotPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }catch(Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}