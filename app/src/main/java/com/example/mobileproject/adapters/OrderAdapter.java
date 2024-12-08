package com.example.mobileproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileproject.OrderDetailsActivity;
import com.example.mobileproject.R;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final Cursor cursor;
    private final Context context;

    public OrderAdapter(Cursor cursor, Context context) {
        this.cursor = cursor;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        cursor.moveToPosition(position);

        int orderId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
        String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

        holder.tvOrderId.setText("Order ID: " + orderId);
        holder.tvOrderTotal.setText("Total: $" + total);
        holder.tvOrderDate.setText("Date: " + date);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra("orderId", orderId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderTotal, tvOrderDate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderTotal = itemView.findViewById(R.id.tv_order_total);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
        }
    }
}
