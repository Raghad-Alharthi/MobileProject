package com.example.mobileproject.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileproject.EditPlantActivity;
import com.example.mobileproject.R;
import com.example.mobileproject.database.DatabaseHelper;
import com.example.mobileproject.models.Plant;

import java.util.List;

public class AdminPlantAdapter extends RecyclerView.Adapter<AdminPlantAdapter.PlantViewHolder> {

    private final List<Plant> plantList;
    private final Context context;
    private final DatabaseHelper databaseHelper;

    public AdminPlantAdapter(List<Plant> plantList, Context context, DatabaseHelper databaseHelper) {
        this.plantList = plantList;
        this.context = context;
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_plant_admin, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plant plant = plantList.get(position);

        holder.plantName.setText(plant.getName());
        holder.plantCategory.setText(plant.getCategory());
        holder.plantPrice.setText(String.format("Price: $%.2f", plant.getPrice()));
        holder.plantQuantity.setText(String.format("Quantity: %d", plant.getQuantityAvailable()));

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditPlantActivity.class);
            intent.putExtra("plantId", plant.getId());
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Plant")
                    .setMessage("Are you sure you want to delete this plant?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        boolean deleted = databaseHelper.deletePlant(plant.getId());
                        if (deleted) {
                            plantList.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Plant deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to delete plant", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    public static class PlantViewHolder extends RecyclerView.ViewHolder {
        TextView plantName, plantCategory, plantPrice, plantQuantity;
        Button btnEdit, btnDelete;

        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            plantName = itemView.findViewById(R.id.tv_plant_name);
            plantCategory = itemView.findViewById(R.id.tv_plant_category);
            plantPrice = itemView.findViewById(R.id.tv_plant_price);
            plantQuantity = itemView.findViewById(R.id.tv_plant_quantity);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
