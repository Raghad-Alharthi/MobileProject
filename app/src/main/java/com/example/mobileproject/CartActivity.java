package com.example.mobileproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileproject.adapters.CartAdapter;
import com.example.mobileproject.database.DatabaseHelper;
import com.example.mobileproject.models.CartItem;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView tvTotalPrice;
    private Button btnCheckout;

    private List<CartItem> cartItemList;
    private CartAdapter cartAdapter;
    private DatabaseHelper databaseHelper;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize DatabaseHelper and FusedLocationProviderClient
        databaseHelper = new DatabaseHelper(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Retrieve User ID
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize Views
        recyclerView = findViewById(R.id.recyclerView);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);

        cartItemList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchCartItems(userId);

        cartAdapter = new CartAdapter(cartItemList, this, this::calculateTotalPrice, databaseHelper);
        recyclerView.setAdapter(cartAdapter);
        calculateTotalPrice();

        ImageButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener((view -> {
            Intent intent = new Intent(CartActivity.this, HomeActivity.class);
            startActivity(intent);
        }));

        // Checkout Button
        btnCheckout.setOnClickListener(view -> {
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            startActivity(intent);
        });
    }

    private void fetchCartItems(int userId) {
        cartItemList.clear();
        Cursor cursor = databaseHelper.getDetailedCartItems(userId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow("product_id"));
                String plantName = cursor.getString(cursor.getColumnIndexOrThrow("plant_name"));
                double plantPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("plant_price"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("cart_quantity"));
                int availableQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity_available"));
                byte[] imageBlob = cursor.getBlob(cursor.getColumnIndexOrThrow("plant_image"));
                Bitmap plantImage = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);

                cartItemList.add(new CartItem(productId, plantName, quantity, plantPrice, availableQuantity, plantImage));
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Notify user if cart is empty
        if (cartItemList.isEmpty()) {
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            btnCheckout.setEnabled(false); // Disable checkout button
        } else {
            btnCheckout.setEnabled(true); // Enable checkout button if items exist
        }
    }


    private double calculateTotalPrice() {
        double totalPrice = 0.0;
        for (CartItem item : cartItemList) {
            totalPrice += item.getPrice() * item.getQuantity();
        }
        tvTotalPrice.setText(String.format("Total: SR %.2f", totalPrice));
        return totalPrice;
    }

}
