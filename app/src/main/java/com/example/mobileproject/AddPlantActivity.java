package com.example.mobileproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileproject.database.DatabaseHelper;

public class AddPlantActivity extends AppCompatActivity {

    private EditText etPlantName, etPlantDescription, etPlantPrice, etPlantQuantity;
    private Spinner spinnerCategory;
    private Button btnAddPlant;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);

        databaseHelper = new DatabaseHelper(this);

        // Initialize UI components
        etPlantName = findViewById(R.id.et_plant_name);
        etPlantDescription = findViewById(R.id.et_plant_description);
        etPlantPrice = findViewById(R.id.et_plant_price);
        etPlantQuantity = findViewById(R.id.et_plant_quantity);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnAddPlant = findViewById(R.id.btn_add_plant);

        btnAddPlant.setOnClickListener(v -> addPlant());
    }

    private void addPlant() {
        String name = etPlantName.getText().toString().trim();
        String description = etPlantDescription.getText().toString().trim();
        String priceStr = etPlantPrice.getText().toString().trim();
        String quantityStr = etPlantQuantity.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        // Validate input
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(quantityStr)) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int quantity = Integer.parseInt(quantityStr);

        // Add plant to database
        long result = databaseHelper.addPlant(name, category, price, quantity);

        if (result > 0) {
            Toast.makeText(this, "Plant added successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Go back to AdminManagePlantsActivity
        } else {
            Toast.makeText(this, "Failed to add plant. Try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
