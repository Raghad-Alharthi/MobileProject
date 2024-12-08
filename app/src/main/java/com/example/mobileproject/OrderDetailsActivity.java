package com.example.mobileproject;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileproject.adapters.OrderItemAdapter;
import com.example.mobileproject.database.DatabaseHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class OrderDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RecyclerView recyclerView;
    private TextView tvOrderId, tvOrderTotal, tvOrderDate;
    private DatabaseHelper databaseHelper;
    private GoogleMap mMap;
    private double latitude = 0.0, longitude = 0.0; // Default location coordinates

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        recyclerView = findViewById(R.id.recycler_view_order_items);
        tvOrderId = findViewById(R.id.tv_order_id);
        tvOrderTotal = findViewById(R.id.tv_order_total);
        tvOrderDate = findViewById(R.id.tv_order_date);

        databaseHelper = new DatabaseHelper(this);

        int orderId = getIntent().getIntExtra("orderId", -1);
        if (orderId == -1) {
            finish(); // Invalid order
        }

        // Initialize Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        loadOrderDetails(orderId);
        loadOrderItems(orderId);
    }

    private void loadOrderDetails(int orderId) {
        Cursor cursor = databaseHelper.getOrderDetails(orderId);
        if (cursor != null && cursor.moveToFirst()) {
            tvOrderId.setText("Order ID: " + cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
            tvOrderTotal.setText("Total: $" + cursor.getDouble(cursor.getColumnIndexOrThrow("total")));
            tvOrderDate.setText("Date: " + cursor.getString(cursor.getColumnIndexOrThrow("date")));

            // Fetch latitude and longitude
            latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
            longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));

            cursor.close();
        }
    }

    private void loadOrderItems(int orderId) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        OrderItemAdapter adapter = new OrderItemAdapter(databaseHelper.getOrderItems(orderId), this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Check if latitude and longitude are valid
        if (latitude != 0.0 && longitude != 0.0) {
            LatLng orderLocation = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions()
                    .position(orderLocation)
                    .title("Order Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(orderLocation, 12));
        } else {
            LatLng defaultLocation = new LatLng(26.4207, 50.0888); // Default to Dammam
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));
        }
    }
}
