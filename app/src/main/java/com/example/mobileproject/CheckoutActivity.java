package com.example.mobileproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileproject.database.DatabaseHelper;
import com.example.mobileproject.models.CartItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {
    private Spinner citySpinner, paymentMethodSpinner;
    private Button btnConfirmOrder;
    private DatabaseHelper databaseHelper;

    private int userId; // User ID from SharedPreferences
    private List<CartItem> cartItems; // Cart items for the current user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        ImageButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener((view -> {
            Intent intent = new Intent(CheckoutActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }));

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Retrieve User ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not logged in. Redirecting...", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Views
        citySpinner = findViewById(R.id.citySpinner);
        paymentMethodSpinner = findViewById(R.id.paymentMethodSpinner);
        btnConfirmOrder = findViewById(R.id.btncheckout);

        // Populate Spinners
        setupSpinners();

        // Fetch Cart Items
        cartItems = fetchCartItems();

        // Handle Confirm Order Button
        btnConfirmOrder.setOnClickListener(view -> confirmOrder());
    }

    private void setupSpinners() {
        // Cities
        List<String> cities = new ArrayList<>();
        cities.add("Dammam");
        cities.add("Al-Ahsa");
        cities.add("Al-Khobar");
        cities.add("Al-Dhahran");
        cities.add("Al-Qatif");

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

        // Payment Methods
        List<String> paymentMethods = new ArrayList<>();
        paymentMethods.add("MADA");
        paymentMethods.add("ApplePay");

        ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentMethods);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentMethodSpinner.setAdapter(paymentAdapter);
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

                // Convert image BLOB to Bitmap (if needed)
                byte[] imageBlob = cursor.getBlob(cursor.getColumnIndexOrThrow("plant_image"));
                Bitmap plantImage = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);

                items.add(new CartItem(productId, plantName, quantity, plantPrice, plantImage));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return items;
    }

    private void confirmOrder() {
        // Validate selections
        String selectedCity = citySpinner.getSelectedItem().toString();
        String selectedPaymentMethod = paymentMethodSpinner.getSelectedItem().toString();

        if (selectedCity.isEmpty() || selectedPaymentMethod.isEmpty()) {
            Toast.makeText(this, "Please select both city and payment method.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Calculate total price
        double totalPrice = 0.0;
        for (CartItem item : cartItems) {
            totalPrice += item.getPrice() * item.getQuantity();
        }

        // Save Order
        long orderId = databaseHelper.addOrder(userId, totalPrice, currentDate, selectedCity, selectedPaymentMethod);

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
