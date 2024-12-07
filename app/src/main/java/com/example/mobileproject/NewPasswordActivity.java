package com.example.mobileproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileproject.database.DatabaseHelper;

public class NewPasswordActivity extends AppCompatActivity {

    private EditText etNewPassword, etConfirmNewPassword;
    private Button btnResetPassword;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        databaseHelper = new DatabaseHelper(this);

        btnResetPassword.setOnClickListener(view -> {
            String newPassword = etNewPassword.getText().toString();
            String confirmPassword = etConfirmNewPassword.getText().toString();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(NewPasswordActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(NewPasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                String email = getIntent().getStringExtra("EMAIL"); // Get email from intent
                String hashedPassword = HashUtils.hashPassword(newPassword);
                boolean updated = databaseHelper.updatePassword(email, hashedPassword);

                if (updated) {
                    Toast.makeText(NewPasswordActivity.this, "Password reset successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NewPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear activity stack
                    startActivity(intent);
                    finish(); // Finish this activity
                } else {
                    Toast.makeText(NewPasswordActivity.this, "Error resetting password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
