package com.example.vroom.activities;

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

public class ForgotPasswordActivity extends AppCompatActivity {
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

        }catch(Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}