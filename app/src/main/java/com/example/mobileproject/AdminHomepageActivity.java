package com.example.mobileproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminHomepageActivity extends AppCompatActivity {

    private Button btnLogout2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_homepage);

        btnLogout2 = findViewById(R.id.btnLogout2);

        // Logout Button Click Listener
        btnLogout2.setOnClickListener(view -> {
            // Redirect to Main Page
            Intent intent = new Intent(AdminHomepageActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close the HomeActivity
        });
    }
}
