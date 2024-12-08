package com.example.mobileproject;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileproject.database.DatabaseHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrderDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseHelper databaseHelper;

    private TextView tvOrderDate, tvOrderTotal,tvOrderItems;

    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        databaseHelper = new DatabaseHelper(this);

        // Initialize views
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvOrderTotal = findViewById(R.id.tvOrderTotal);
        tvOrderItems = findViewById(R.id.tvOrderItems);

        // Get order details passed via intent
        int orderId = getIntent().getIntExtra("order_id", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Order ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load order details
        loadOrderDetails(orderId);

        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void loadOrderDetails(int orderId) {
        Cursor orderCursor = databaseHelper.getOrderDetails(orderId);
        if (orderCursor != null && orderCursor.moveToFirst()) {
            String date = orderCursor.getString(orderCursor.getColumnIndexOrThrow("date"));
            double total = orderCursor.getDouble(orderCursor.getColumnIndexOrThrow("total"));
            latitude = orderCursor.getDouble(orderCursor.getColumnIndexOrThrow("latitude"));
            longitude = orderCursor.getDouble(orderCursor.getColumnIndexOrThrow("longitude"));

            tvOrderDate.setText("Date: " + date);
            tvOrderTotal.setText("Total: SR " + String.format(Locale.getDefault(), "%.2f", total));

            loadOrderItems(orderId);
            orderCursor.close();
        }
    }

    private void loadOrderItems(int orderId) {
        Cursor itemsCursor = databaseHelper.getOrderItems(orderId);
        StringBuilder itemsDetails = new StringBuilder();
        if (itemsCursor != null && itemsCursor.moveToFirst()) {
            do {
                String productName = itemsCursor.getString(itemsCursor.getColumnIndexOrThrow("name"));
                int quantity = itemsCursor.getInt(itemsCursor.getColumnIndexOrThrow("quantity"));
                double price = itemsCursor.getDouble(itemsCursor.getColumnIndexOrThrow("price"));

                itemsDetails.append(productName)
                        .append(" (x").append(quantity).append("): SR ")
                        .append(String.format(Locale.getDefault(), "%.2f", price))
                        .append("\n");
            } while (itemsCursor.moveToNext());
            itemsCursor.close();
        }
        tvOrderItems.setText(itemsDetails.toString());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker for the order location
        LatLng orderLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(orderLocation).title("Order Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(orderLocation, 15));
    }
}
