package com.example.mobileproject.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileproject.R;
import com.example.mobileproject.database.DatabaseHelper;
import com.example.mobileproject.models.Plant;

import java.util.List;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.ViewHolder> {

    private List<Plant> plantList;
    private final Context context;
    private final DatabaseHelper databaseHelper;

    public PlantAdapter(List<Plant> plantList, Context context, DatabaseHelper databaseHelper) {
        this.plantList = plantList;
        this.context = context;
        this.databaseHelper = databaseHelper;
    }

    public void updatePlants(List<Plant> newPlantList) {
        this.plantList = newPlantList; // Directly assign the new list
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Plant plant = plantList.get(position);
        Log.d("PlantAdapter", "Position: " + position + ", Plant: " + plant.getName());

        // Set plant name and price
        holder.plantNameTextView.setText(plant.getName());
        holder.plantPriceTextView.setText(String.format("SR %.2f", plant.getPrice()));

        // Decode image BLOB to Bitmap
        byte[] imageBlob = plant.getImageBlob();
        if (imageBlob != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);
            holder.plantImageView.setImageBitmap(bitmap);
        }

        // Handle Add to Cart click
        holder.addToCartIcon.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt("user_id", -1);

            if (userId != -1) {
                // Add item to the cart
                databaseHelper.addToCart(userId, plant.getId(), 1); // Add 1 item to the cart
                Toast.makeText(context, "Item added to cart", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView plantNameTextView;
        TextView plantPriceTextView;
        ImageView plantImageView;
        ImageView addToCartIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            plantNameTextView = itemView.findViewById(R.id.plant_name);
            plantPriceTextView = itemView.findViewById(R.id.plant_price);
            plantImageView = itemView.findViewById(R.id.plant_image);
            addToCartIcon = itemView.findViewById(R.id.addToCartIcon);
        }
    }
}
