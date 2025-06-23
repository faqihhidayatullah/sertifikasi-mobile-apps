package com.example.product_bottomnav.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sertifikasi.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private List<OrderItem> orderItems;
    private OrderHelper orderHelper;
    private OnOrderChangedListener orderChangedListener;

    // Interface untuk notifikasi perubahan
    public interface OnOrderChangedListener {
        void onOrderChanged();
    }

    public OrderAdapter(List<OrderItem> orderItems, OrderHelper orderHelper, OnOrderChangedListener listener) {
        this.orderItems = orderItems;
        this.orderHelper = orderHelper;
        this.orderChangedListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);

        // Set data ke views
        holder.tvProductName.setText(item.getMerk());
        holder.tvPrice.setText(String.format("Rp %,.0f", item.getHargajual()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvSubtotal.setText(String.format("Subtotal: Rp %,.0f", item.getSubtotal()));

        // Load image dengan Glide
        Glide.with(holder.itemView.getContext())
                .load("https://posyandukorowelangkulon.my.id/sertifikasi/Gambar_Product/" + item.getFoto())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.imgProduct);

        // Tambah jumlah (+)
        holder.btnIncrease.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            orderHelper.updateQuantity(item.getKode(), item.getQuantity());

            holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
            holder.tvPrice.setText(String.format("Rp %,.0f", item.getHargajual()));
            holder.tvSubtotal.setText(String.format("Subtotal: Rp %,.0f", item.getSubtotal()));

            if (orderChangedListener != null) {
                orderChangedListener.onOrderChanged();
            }
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                orderHelper.updateQuantity(item.getKode(), item.getQuantity());

                holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
                holder.tvPrice.setText(String.format("Rp %,.0f", item.getHargajual()));
                holder.tvSubtotal.setText(String.format("Subtotal: Rp %,.0f", item.getSubtotal()));

                if (orderChangedListener != null) {
                    orderChangedListener.onOrderChanged();
                }
            }
        });


        // Hapus item
        holder.btnRemove.setOnClickListener(v -> {
            orderHelper.removeFromOrder(item.getKode());
            orderItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, orderItems.size());

            if (orderChangedListener != null) {
                orderChangedListener.onOrderChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvPrice, tvQuantity, tvSubtotal;
        ImageButton btnIncrease, btnDecrease, btnRemove;
        ImageView imgProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            imgProduct = itemView.findViewById(R.id.imgProduct);
        }
    }
}