package com.example.mobileproject;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileproject.adapters.OrderAdapter;
import com.example.mobileproject.database.DatabaseHelper;
import com.example.mobileproject.models.Order;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView ordersRecyclerView;
    private DatabaseHelper databaseHelper;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        databaseHelper = new DatabaseHelper(this);
        ordersRecyclerView = findViewById(R.id.OrderRV);

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId != -1) {
            orderList = fetchUserOrders(userId);
            if (orderList.isEmpty()) {
                Toast.makeText(this, "No orders found.", Toast.LENGTH_SHORT).show();
            } else {
                ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                ordersRecyclerView.setAdapter(new OrderAdapter(orderList, this));
            }
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private List<Order> fetchUserOrders(int userId) {
        List<Order> orders = new ArrayList<>();
        Cursor cursor = databaseHelper.getUserOrders(userId);

        if (cursor == null) {
            Log.e("HistoryActivity", "Cursor is null. No data found.");
            return orders;
        }

        if (cursor.moveToFirst()) {
            do {
                int orderId = cursor.getInt(cursor.getColumnIndexOrThrow("order_id"));
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                orders.add(new Order(orderId, total, date));
                Log.d("HistoryActivity", "Fetched order: " + orderId);
            } while (cursor.moveToNext());
        } else {
            Log.d("HistoryActivity", "No orders found for userId: " + userId);
        }

        cursor.close();
        return orders;
    }
}
