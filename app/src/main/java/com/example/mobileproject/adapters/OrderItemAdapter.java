package com.example.mobileproject.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileproject.R;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private final Cursor cursor;
    private final Context context;

    public OrderItemAdapter(Cursor cursor, Context context) {
        this.cursor = cursor;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        cursor.moveToPosition(position);

        String itemName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        double itemPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

        holder.tvItemName.setText(itemName);
        holder.tvItemPrice.setText("Price: $" + itemPrice);
        holder.tvQuantity.setText("Quantity: " + quantity);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemPrice, tvQuantity;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemPrice = itemView.findViewById(R.id.tv_item_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
        }
    }
}
