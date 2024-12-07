package com.example.mobileproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileproject.adapters.PlantAdapter;
import com.example.mobileproject.database.DatabaseHelper;
import com.example.mobileproject.models.Plant;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private List<Plant> plantList = new ArrayList<>();
    private PlantAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Apply edge-to-edge design
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get logged-in user ID (optional for this activity)
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        // Initialize database and UI components
        dbHelper = new DatabaseHelper(this);

        RecyclerView recyclerView = findViewById(R.id.plant_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Two items per row

        // Load default plants (Indoor)
        plantList = dbHelper.getIndoorPlants();
        adapter = new PlantAdapter(plantList, this, dbHelper);
        recyclerView.setAdapter(adapter);

        // Cart button click listener
        ImageView cart = findViewById(R.id.CartIcon);
        cart.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // Plant type toggle switch
        Switch plantTypeSwitch = findViewById(R.id.switch2);

        plantTypeSwitch.setChecked(false); // Default to Indoor plants
        plantTypeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            List<Plant> updatedList;
            if (isChecked) {
                // Show Outdoor plants
                updatedList = dbHelper.getOutdoorPlants();
                Toast.makeText(this, "Outdoor Plants", Toast.LENGTH_SHORT).show();
            } else {
                // Show Indoor plants
                updatedList = dbHelper.getIndoorPlants();
                Toast.makeText(this, "Indoor Plants", Toast.LENGTH_SHORT).show();
            }
            adapter.updatePlants(updatedList); // Update RecyclerView data
        });
    }
}
