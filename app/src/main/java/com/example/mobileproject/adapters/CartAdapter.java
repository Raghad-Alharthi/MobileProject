package com.example.mobileproject.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileproject.database.DatabaseHelper;
import com.example.mobileproject.models.CartItem;
import com.example.mobileproject.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final List<CartItem> cartItemList;
    private final Context context;
    private final Runnable onQuantityChange;
    private final DatabaseHelper databaseHelper;
    private int userId;

    public CartAdapter(List<CartItem> cartItemList, Context context, Runnable onQuantityChange, DatabaseHelper databaseHelper) {
        this.cartItemList = cartItemList;
        this.context = context;
        this.onQuantityChange = onQuantityChange;
        this.databaseHelper = databaseHelper;

        // Retrieve userId from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        this.userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(context, "User not logged in!", Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);

        // Set item details
        holder.tvName.setText(item.getName());
        holder.tvPrice.setText(String.format("SR %.2f", item.getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.ivImage.setImageBitmap(item.getImage());

        // Handle Increase Button
        holder.btnIncrease.setOnClickListener(v -> {
            if (item.getQuantity() < item.getAvailableQuantity()) { // Check available stock
                item.setQuantity(item.getQuantity() + 1); // Increase quantity
                databaseHelper.updateCartItemQuantity(userId, item.getProductId(), item.getQuantity()); // Update database
                notifyItemChanged(position); // Refresh RecyclerView item
                onQuantityChange.run(); // Recalculate total price
            } else {
                Toast.makeText(context, "Maximum available quantity reached!", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Decrease Button
        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1); // Decrease quantity
                databaseHelper.updateCartItemQuantity(userId, item.getProductId(), item.getQuantity()); // Update database
                notifyItemChanged(position); // Refresh RecyclerView item
                onQuantityChange.run(); // Recalculate total price
            } else {
                // Confirm deletion when quantity reaches 0
                new AlertDialog.Builder(context)
                        .setTitle("Confirm Deletion")
                        .setMessage("Do you want to remove this item from the cart?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Remove item from database
                            databaseHelper.deleteCartItem(userId, item.getProductId());
                            cartItemList.remove(position); // Remove item from list
                            notifyItemRemoved(position); // Notify RecyclerView
                            onQuantityChange.run(); // Recalculate total price
                            Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity;
        ImageView ivImage;
        ImageButton btnIncrease, btnDecrease;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvID);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            ivImage = itemView.findViewById(R.id.ivImage);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
        }
    }
}
