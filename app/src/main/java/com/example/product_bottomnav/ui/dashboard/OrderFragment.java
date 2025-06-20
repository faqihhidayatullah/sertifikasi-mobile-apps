package com.example.product_bottomnav.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.product_bottomnav.LoginActivity;
import com.example.product_bottomnav.MainActivity;
import com.example.product_bottomnav.databinding.FragmentOrderBinding;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment implements OrderAdapter.OnOrderChangedListener {

    private FragmentOrderBinding binding;
    private OrderHelper orderHelper;
    private OrderAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup RecyclerView
        binding.recyclerViewOrders.setNestedScrollingEnabled(false);
        binding.recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        // Ambil email user dari SharedPreferences
        SharedPreferences userPrefs = requireContext().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        String storedEmail = userPrefs.getString("email", "guest@example.com");

        // Init orderHelper dengan email
        orderHelper = new OrderHelper(requireContext(), storedEmail);

        // Load dan tampilkan daftar pesanan
        setupRecyclerView();

        // Setup tombol Bayar
        binding.btnBayar.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
            String email = sharedPreferences.getString("email", null);
            String userType = sharedPreferences.getString("userType", null);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

            if (!isLoggedIn || email == null || email.isEmpty() || "guest".equals(userType)) {
                // Bersihkan sesi dan arahkan ke Login
                sharedPreferences.edit().clear().apply();

                Toast.makeText(getContext(), "Sesi login tidak ditemukan, silakan login kembali", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
                return;
            }

            // Ambil daftar item pesanan
            List<OrderItem> orderItems = orderHelper.getOrderItems();
            if (orderItems.isEmpty()) {
                Toast.makeText(getContext(), "Pesanan kosong, silakan tambah produk dulu", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lanjut ke KonfirmasiPesananActivity
            Intent intent = new Intent(getContext(), KonfirmasiPesananActivity.class);
            intent.putParcelableArrayListExtra("order_items", new ArrayList<>(orderItems));
            intent.putExtra("total_harga", orderHelper.getTotal());
            startActivity(intent);
        });

        // Update badge cart awal
        updateCartBadge();

        return root;
    }

    private void setupRecyclerView() {
        List<OrderItem> orderItems = orderHelper.getOrderItems();
        adapter = new OrderAdapter(orderItems, orderHelper, this);
        binding.recyclerViewOrders.setAdapter(adapter);
        updateTotal();
    }

    private void updateTotal() {
        binding.tvTotal.setText(String.format("Total Bayar: Rp %,.0f", orderHelper.getTotal()));
    }

    private void updateCartBadge() {
        if (getActivity() instanceof MainActivity) {
            int totalQty = orderHelper.getTotalQuantity();
            ((MainActivity) getActivity()).updateCartBadge(totalQty);
        }
    }

    @Override
    public void onOrderChanged() {
        updateTotal();
        updateCartBadge(); // Update badge ketika ada perubahan jumlah item
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCartBadge(); // Update badge saat kembali ke fragment
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
