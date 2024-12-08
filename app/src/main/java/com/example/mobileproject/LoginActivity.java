package com.example.mobileproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileproject.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignup;
    private TextView tvForgotPassword;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Initialize Database Helper
        databaseHelper = new DatabaseHelper(this);

        // Login Button Click Listener
        btnLogin.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Hash the entered password
                String hashedPassword = HashUtils.hashPassword(password);

                // Verify credentials with the database
                String storedPassword = databaseHelper.getUserPassword(email);
                String role = databaseHelper.getUserRole(email);

                if (storedPassword != null && storedPassword.equals(hashedPassword)) {
                    int userId = databaseHelper.getUserIdByEmail(email);

                    // Save User ID in SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("user_id", userId);
                    editor.apply();
                    Log.d("LoginActivity", "Saved user_id: " + userId);

                    if ("admin".equals(role)) {
                        // Redirect to Admin Homepage
                        Toast.makeText(LoginActivity.this, "Admin Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, AdminManagePlantsActivity.class);
                        startActivity(intent);
                        finish(); // Close LoginActivity
                    } else if ("user".equals(role)) {
                        // Redirect to User Homepage
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("USER_EMAIL", email); // Pass the logged-in user's email
                        startActivity(intent);
                        finish(); // Close LoginActivity
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Role", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Redirect to Signup Screen
        tvSignup.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // Redirect to Reset Password Activity
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });
    }
}
