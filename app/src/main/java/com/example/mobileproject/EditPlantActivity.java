package com.example.mobileproject;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mobileproject.database.DatabaseHelper;
import com.example.mobileproject.models.Plant;

public class EditPlantActivity extends AppCompatActivity {

    private EditText plantName, plantQuantity, plantPrice, plantCategory;
    private DatabaseHelper databaseHelper;
    private int plantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plant);

        databaseHelper = new DatabaseHelper(this);

        plantName = findViewById(R.id.plant_name);
        plantQuantity = findViewById(R.id.plant_quantity);
        plantPrice = findViewById(R.id.plant_price);
        plantCategory = findViewById(R.id.plant_category);

        // Get Plant ID from Intent
        plantId = getIntent().getIntExtra("plant_id", -1);
        if (plantId != -1) {
            Plant plant = databaseHelper.getPlantById(plantId);
            if (plant != null) {
                plantName.setText(plant.getName());
                plantQuantity.setText(String.valueOf(plant.getQuantityAvailable()));
                plantPrice.setText(String.valueOf(plant.getPrice()));
                plantCategory.setText(plant.getCategory());
            }
        }

        findViewById(R.id.editbtn).setOnClickListener(view -> {
            String name = plantName.getText().toString().trim();
            String quantityStr = plantQuantity.getText().toString().trim();
            String priceStr = plantPrice.getText().toString().trim();
            String category = plantCategory.getText().toString().trim();

            if (!name.isEmpty() && !quantityStr.isEmpty() && !priceStr.isEmpty() && !category.isEmpty()) {
                int quantity = Integer.parseInt(quantityStr);
                double price = Double.parseDouble(priceStr);
                databaseHelper.updatePlant(plantId, name, category, price, quantity);
                Toast.makeText(EditPlantActivity.this, "Plant updated successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Go back to AdminManageItemsActivity
            } else {
                Toast.makeText(EditPlantActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
