package com.example.mobileproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class AdminPlantAdapter extends RecyclerView.Adapter<AdminPlantAdapter.ViewHolder> {

    private List<Plant> plantList;
    private final Context context;
    private final DatabaseHelper databaseHelper;

    public AdminPlantAdapter(List<Plant> plantList, Context context, DatabaseHelper databaseHelper) {
        this.plantList = plantList;
        this.context = context;
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Plant plant = plantList.get(position);

        holder.plantName.setText(plant.getName());
        holder.plantPrice.setText(String.format("SR %.2f", plant.getPrice()));
        holder.plantQuantity.setText(String.format("Qty: %d", plant.getQuantityAvailable()));

        byte[] imageBlob = plant.getImageBlob();
        if (imageBlob != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);
            holder.plantImage.setImageBitmap(bitmap);
        }

        holder.editIcon.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditPlantActivity.class);
            intent.putExtra("plant_id", plant.getId());
            context.startActivity(intent);
        });

        holder.deleteIcon.setOnClickListener(v -> {
            boolean isDeleted = databaseHelper.deletePlant(plant.getId());
            if (isDeleted) {
                plantList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, plantList.size());
                Toast.makeText(context, "Plant deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete plant", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    public void updatePlants(List<Plant> newPlantList) {
        this.plantList = newPlantList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView plantName;
        TextView plantPrice;
        TextView plantQuantity;
        ImageView plantImage;
        ImageView editIcon;
        ImageView deleteIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            plantName = itemView.findViewById(R.id.name);
            plantPrice = itemView.findViewById(R.id.price);
            plantQuantity = itemView.findViewById(R.id.quantity);
            plantImage = itemView.findViewById(R.id.imageview);
            editIcon = itemView.findViewById(R.id.edit_icon);
            deleteIcon = itemView.findViewById(R.id.delete_icon);
        }
    }
}
