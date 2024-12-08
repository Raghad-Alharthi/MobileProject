package com.example.mobileproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileproject.adapters.AdminPlantAdapter;
import com.example.mobileproject.database.DatabaseHelper;
import com.example.mobileproject.models.Plant;

import java.util.List;

public class AdminManageItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminPlantAdapter adapter;
    private DatabaseHelper databaseHelper;
    private TextView emptyStateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_items);

        databaseHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadPlants();

        findViewById(R.id.add_new_item_button).setOnClickListener(view -> {
            Intent intent = new Intent(AdminManageItemsActivity.this, AddPlantActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlants();
    }

    private void loadPlants() {
        List<Plant> plants = databaseHelper.getAllPlants();

        emptyStateView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        adapter = new AdminPlantAdapter(plants, this, databaseHelper);
        recyclerView.setAdapter(adapter);

    }
}
