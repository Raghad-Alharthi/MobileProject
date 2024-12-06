package com.example.mobileproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private Button btnSignup;
    private UserDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignup = findViewById(R.id.btnSignup);

        // Initialize Database Helper
        databaseHelper = new UserDatabaseHelper(this);

        // Set onClick Listener for Signup Button
        btnSignup.setOnClickListener(view -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            // Validate input fields
            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(SignupActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (databaseHelper.checkUserExists(email)) {
                Toast.makeText(SignupActivity.this, "User already exists with this email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Hash the password using HashUtils
            String hashedPassword = HashUtils.hashPassword(password);

            // Save user data to the database
            boolean isInserted = databaseHelper.addUser(fullName, email, hashedPassword);

            if (isInserted) {
                Toast.makeText(SignupActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                finish(); // Close the SignupActivity and go back to LoginActivity
            } else {
                Toast.makeText(SignupActivity.this, "Error occurred during signup", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
