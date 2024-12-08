package com.example.mobileproject;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mobileproject.database.DatabaseHelper;

public class AddPlantActivity extends AppCompatActivity {

    private EditText plantName, plantQuantity, plantPrice, plantCategory;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);

        databaseHelper = new DatabaseHelper(this);

        plantName = findViewById(R.id.plant_name);
        plantQuantity = findViewById(R.id.plant_quantity);
        plantPrice = findViewById(R.id.plant_price);
        plantCategory = findViewById(R.id.plant_category);

        findViewById(R.id.add_plant_button).setOnClickListener(view -> {
            String name = plantName.getText().toString().trim();
            String quantityStr = plantQuantity.getText().toString().trim();
            String priceStr = plantPrice.getText().toString().trim();
            String category = plantCategory.getText().toString().trim();

            if (!name.isEmpty() && !quantityStr.isEmpty() && !priceStr.isEmpty() && !category.isEmpty()) {
                int quantity = Integer.parseInt(quantityStr);
                double price = Double.parseDouble(priceStr);
                databaseHelper.addPlant(name, category, price, quantity);
                Toast.makeText(AddPlantActivity.this, "Plant added successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Go back to AdminManageItemsActivity
            } else {
                Toast.makeText(AddPlantActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
