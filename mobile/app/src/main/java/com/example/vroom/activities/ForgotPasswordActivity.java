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
import com.example.vroom.DTOs.auth.requests.ResetPasswordRequestDTO;
import com.example.vroom.R;
import com.example.vroom.network.RetrofitClient;
import com.example.vroom.viewmodels.ForgotPasswordViewModel;
import com.example.vroom.viewmodels.LoginViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends BaseActivity {
    private EditText emailInput;
    private EditText codeInput;
    private EditText passInput;
    private EditText rePassInput;
    private Button submitBtn;
    private ForgotPasswordViewModel viewModel;

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

        emailInput = findViewById(R.id.email_input);
        codeInput = findViewById(R.id.code_input);
        passInput = findViewById(R.id.password_input);
        rePassInput = findViewById(R.id.confirm_password_input);

        submitBtn = findViewById(R.id.submit_button);

        submitBtn.setOnClickListener(v -> resetPass());

        viewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);
        observeViewModel();
    }
    private void observeViewModel(){
        viewModel.getResetPasswordMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getResetPasswordStatus().observe(this, success -> {
            if (success != null && success) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void resetPass(){
        String email = emailInput.getText().toString().trim();
        String code = codeInput.getText().toString().trim();
        String pass = passInput.getText().toString().trim();
        String rePass = rePassInput.getText().toString().trim();

        viewModel.resetPassword(email, code, pass, rePass);
    }
}