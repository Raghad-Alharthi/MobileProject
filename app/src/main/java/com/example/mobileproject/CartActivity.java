package com.example.mobileproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileproject.adapters.CartAdapter;
import com.example.mobileproject.models.CartItem;
import com.example.mobileproject.R;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView tvTotalPrice;
    private Button btnCheckout;
    private List<CartItem> cartItemList;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample Data for Cart Items
        cartItemList = new ArrayList<>();
        cartItemList.add(new CartItem("Bonsai Tree", "100 x 120 cm", 1, 300.0, R.drawable.bonsai_tree));
        cartItemList.add(new CartItem("Ficus Lyrata Bambino", "80 x 100 cm", 1, 79.0, R.drawable.ficus_lyrata));
        cartItemList.add(new CartItem("Zamioculcas Zamifolia", "70 x 125 cm", 1, 139.0, R.drawable.zamioculcas));

        // Set Adapter
        cartAdapter = new CartAdapter(cartItemList, this, this::calculateTotalPrice);
        recyclerView.setAdapter(cartAdapter);

        // Calculate Initial Total Price
        calculateTotalPrice();
    }

    // Method to Calculate Total Price
    private void calculateTotalPrice() {
        double totalPrice = 0.0;
        for (CartItem item : cartItemList) {
            totalPrice += item.getPrice() * item.getQuantity();
        }
        tvTotalPrice.setText(String.format("Total: SR %.2f", totalPrice));
    }
}
