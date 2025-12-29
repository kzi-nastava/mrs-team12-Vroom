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

import com.example.vroom.R;

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
    }

    private void forgotPassReq(){
        try{
            String email = emailInput.getText().toString().trim();

            if (email.isEmpty())
                throw new Exception("Email is missing");


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

        }catch(Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}