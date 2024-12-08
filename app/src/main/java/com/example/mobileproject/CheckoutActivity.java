package com.example.mobileproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.mobileproject.database.DatabaseHelper;
import com.example.mobileproject.models.CartItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng selectedLocation;
    private Button btnConfirmOrder;
    private DatabaseHelper databaseHelper;

    private int userId; // User ID from SharedPreferences
    private List<CartItem> cartItems; // Cart items for the current user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Back Button
        ImageButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(CheckoutActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Retrieve User ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not logged in. Redirecting...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }



        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Confirm Order Button
        btnConfirmOrder = findViewById(R.id.btncheckoutConfirm);
        cartItems = fetchCartItems();

        btnConfirmOrder.setOnClickListener(view -> {
            if (selectedLocation != null) {
                confirmOrder(selectedLocation.latitude, selectedLocation.longitude);
            } else {
                Toast.makeText(this, "Please select a location on the map", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Default location (e.g., Dammam)
        LatLng defaultLocation = new LatLng(26.4207, 50.0888);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));

        // Add marker on map click
        mMap.setOnMapClickListener(latLng -> {
            mMap.clear(); // Clear previous markers
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            if (marker != null) marker.showInfoWindow();
            selectedLocation = latLng;
        });
    }

    private List<CartItem> fetchCartItems() {
        List<CartItem> items = new ArrayList<>();
        Cursor cursor = databaseHelper.getDetailedCartItems(userId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow("product_id"));
                String plantName = cursor.getString(cursor.getColumnIndexOrThrow("plant_name"));
                double plantPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("plant_price"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("cart_quantity"));

                // Convert image BLOB to Bitmap
                byte[] imageBlob = cursor.getBlob(cursor.getColumnIndexOrThrow("plant_image"));
                Bitmap plantImage = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);

                items.add(new CartItem(productId, plantName, quantity, plantPrice, plantImage));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return items;
    }

    private void confirmOrder(double latitude, double longitude) {
        // Get current date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Calculate total price
        double totalPrice = 0.0;
        for (CartItem item : cartItems) {
            totalPrice += item.getPrice() * item.getQuantity();
        }

        // Save Order
        long orderId = databaseHelper.addOrder(userId, totalPrice, currentDate, latitude, longitude);

        if (orderId != -1) {
            // Save each cart item as order item
            for (CartItem item : cartItems) {
                databaseHelper.addOrderItem((int) orderId, item.getProductId(), item.getQuantity());
            }

            // Clear Cart
            databaseHelper.clearCart(userId);
            Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();

            // Redirect to HomeActivity
            Intent intent = new Intent(CheckoutActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to place order. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
