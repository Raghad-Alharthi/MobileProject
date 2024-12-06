package com.example.mobileproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VerifyCodeActivity extends AppCompatActivity {

    private EditText etVerificationCode;
    private Button btnVerifyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        etVerificationCode = findViewById(R.id.etVerificationCode);
        btnVerifyCode = findViewById(R.id.btnVerifyCode);

        String expectedCode = getIntent().getStringExtra("CODE");
        String email = getIntent().getStringExtra("EMAIL");

        btnVerifyCode.setOnClickListener(view -> {
            String enteredCode = etVerificationCode.getText().toString().trim();

            if (enteredCode.equals(expectedCode)) {
                Toast.makeText(VerifyCodeActivity.this, "Code verified!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(VerifyCodeActivity.this, NewPasswordActivity.class);
                intent.putExtra("EMAIL", email);
                startActivity(intent);
            } else {
                Toast.makeText(VerifyCodeActivity.this, "Invalid code. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
