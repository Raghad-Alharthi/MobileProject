package com.example.mobileproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileproject.database.DatabaseHelper;
import com.example.mobileproject.models.Plant;

public class EditPlantActivity extends AppCompatActivity {

    private EditText etPlantName, etPlantDescription, etPlantPrice, etPlantQuantity;
    private Spinner spinnerCategory;
    private Button btnSaveChanges;
    private DatabaseHelper databaseHelper;
    private int plantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plant);

        databaseHelper = new DatabaseHelper(this);

        // Initialize UI components
        etPlantName = findViewById(R.id.et_plant_name);
        etPlantDescription = findViewById(R.id.et_plant_description);
        etPlantPrice = findViewById(R.id.et_plant_price);
        etPlantQuantity = findViewById(R.id.et_plant_quantity);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnSaveChanges = findViewById(R.id.btn_save_changes);

        // Get the plant ID from the intent
        plantId = getIntent().getIntExtra("plantId", -1);
        if (plantId == -1) {
            Toast.makeText(this, "Error: Plant not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load plant details
        loadPlantDetails();

        // Save changes when the button is clicked
        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void loadPlantDetails() {
        Plant plant = databaseHelper.getPlantById(plantId);
        if (plant != null) {
            etPlantName.setText(plant.getName());
            etPlantDescription.setText(plant.getDescription());
            etPlantPrice.setText(String.valueOf(plant.getPrice()));
            etPlantQuantity.setText(String.valueOf(plant.getQuantityAvailable()));

            // Set category in spinner
            String[] categories = getResources().getStringArray(R.array.plant_categories);
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equalsIgnoreCase(plant.getCategory())) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
        } else {
            Toast.makeText(this, "Error: Could not load plant details.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void saveChanges() {
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

        // Update plant in the database
        int rowsAffected = databaseHelper.updatePlant(plantId, name, category, price, quantity);

        if (rowsAffected > 0) {
            Toast.makeText(this, "Plant updated successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Return to the previous screen
        } else {
            Toast.makeText(this, "Failed to update plant. Try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
