package com.example.mobileproject;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
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
    private Button btnLogout;

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
        adapter = new PlantAdapter(plantList, this, dbHelper, this::showPlantDetailsPopup);
        recyclerView.setAdapter(adapter);

        // Cart button click listener
        ImageView cart = findViewById(R.id.CartIcon);
        cart.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);
        });

        ImageView history = findViewById(R.id.historyIcon);
        history.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, OrdersActivity.class);
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

        btnLogout = findViewById(R.id.btnLogout);
        // Logout Button Click Listener
        btnLogout.setOnClickListener(view -> {
            // Redirect to Main Page
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close the HomeActivity
        });
    }

    private void showPlantDetailsPopup(Plant plant) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_plant_details);

        TextView plantName = dialog.findViewById(R.id.plant_name);
        TextView plantDes = dialog.findViewById(R.id.plant_des);
        TextView plantPrice = dialog.findViewById(R.id.plant_price);
        ImageView plantImage = dialog.findViewById(R.id.plant_image);
        //TextView quantityInput = dialog.findViewById(R.id.quantity_input);
        Button addToCartButton = dialog.findViewById(R.id.add_to_cart_button);
//        Button increaseQuantity = dialog.findViewById(R.id.btnIncrease2);
//        Button decreaseQuantity = dialog.findViewById(R.id.btnDecrease2);

        // Populate plant details
        plantName.setText(plant.getName());
        plantDes.setText(plant.getDescription());
        plantPrice.setText(String.format("SR %.2f", plant.getPrice()));
        byte[] imageBlob = plant.getImageBlob();
        if (imageBlob != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);
            plantImage.setImageBitmap(bitmap);
        }




        // Handle "Add to Cart" button
        addToCartButton.setOnClickListener(v -> {

                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                int userId = sharedPreferences.getInt("user_id", -1);
                if (userId != -1) {
                    dbHelper.addToCart(userId, plant.getId(), 1);
                    Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
        });

        dialog.show();
    }
}
