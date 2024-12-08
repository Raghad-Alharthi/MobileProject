package com.example.mobileproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileproject.adapters.AdminPlantAdapter;
import com.example.mobileproject.database.DatabaseHelper;
import com.example.mobileproject.models.Plant;

import java.util.List;

public class AdminManagePlantsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminPlantAdapter adapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_plants);

        recyclerView = findViewById(R.id.recycler_view);
        Button addNewPlantButton = findViewById(R.id.btn_add_new_plant);

        databaseHelper = new DatabaseHelper(this);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadPlants();

        // Navigate to AddPlantActivity
        addNewPlantButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminManagePlantsActivity.this, AddPlantActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload plants when returning to this activity
        loadPlants();
    }

    private void loadPlants() {
        List<Plant> plants = databaseHelper.getAllPlants();
        adapter = new AdminPlantAdapter(plants, this, databaseHelper);
        recyclerView.setAdapter(adapter);

        if (plants.isEmpty()) {
            Toast.makeText(this, "No plants found. Add new plants!", Toast.LENGTH_SHORT).show();
        }
    }
}
