package com.example.mobileproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnLogout;
    private UserDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Views
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnLogout);

        // Initialize Database Helper
        databaseHelper = new UserDatabaseHelper(this);

        // Get User Email from Intent
        String userEmail = getIntent().getStringExtra("USER_EMAIL");

        // Fetch and Set Welcome Message
        if (userEmail != null) {
            String userName = databaseHelper.getUserName(userEmail); // Fetch the user's name
            if (userName != null) {
                tvWelcome.setText("Welcome, " + userName + "!");
            } else {
                tvWelcome.setText("Welcome!");
            }
        }

        // Logout Button Click Listener
        btnLogout.setOnClickListener(view -> {
            // Redirect to Main Page
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close the HomeActivity
        });
    }
}
