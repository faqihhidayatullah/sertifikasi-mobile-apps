package com.example.product_bottomnav.ui.product;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.product_bottomnav.R;
import com.example.product_bottomnav.databinding.FragmentProductBinding;
import com.example.product_bottomnav.ui.dashboard.OrderHelper;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFragment extends Fragment {

    private FragmentProductBinding binding;
    private ProductAdapter adapter;
    private OrderHelper orderHelper;
    private List<Product> productList;
    private String selectedCategory = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences userPrefs = requireContext().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        String email = userPrefs.getString("email", "guest@example.com");
        orderHelper = new OrderHelper(requireContext(), email);

        // Setup RecyclerView
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Setup SwipeRefresh
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            // Sembunyikan produk & tampilkan loading
            binding.recyclerView.setVisibility(View.GONE);
            binding.loadingAnimation.setVisibility(View.VISIBLE);
            binding.loadingAnimation.playAnimation();

            // Kosongkan adapter sementara agar produk benar-benar "hilang"
            if (adapter != null) {
                adapter.updateData(new ArrayList<>());
            }

            fetchProduct();
        });

        // Setup Search
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (adapter != null) {
                    adapter.filter(query, selectedCategory);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.filter(newText, selectedCategory);
                }
                return true;
            }
        });

        // Kategori Filter
        binding.btnAll.setOnClickListener(v -> {
            filterByCategory("");
            resetButtonStyles();
            highlightSelectedButton(binding.btnAll);
        });

        binding.btnSelempang.setOnClickListener(v -> {
            filterByCategory("kemeja");
            resetButtonStyles();
            highlightSelectedButton(binding.btnSelempang);
        });

        binding.btnHandbag.setOnClickListener(v -> {
            filterByCategory("dress");
            resetButtonStyles();
            highlightSelectedButton(binding.btnHandbag);
        });

        binding.btnTotebag.setOnClickListener(v -> {
            filterByCategory("kain");
            resetButtonStyles();
            highlightSelectedButton(binding.btnTotebag);
        });

        // Default highlight
        resetButtonStyles();
        highlightSelectedButton(binding.btnAll);

        // Ambil data awal
        fetchProduct();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadProductData();
    }

    private void fetchProduct() {
        RegisterAPI apiService = RetrofitClient.getRetrofitInstance().create(RegisterAPI.class);
        Call<List<Product>> call = apiService.getProducts();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                // Matikan loading
                binding.swipeRefreshLayout.setRefreshing(false);
                binding.loadingAnimation.setVisibility(View.GONE);
                binding.loadingAnimation.cancelAnimation();
                binding.recyclerView.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    productList = response.body();
                    adapter = new ProductAdapter(getContext(), productList, product -> {
                        orderHelper.addToOrder(product);
                        Toast.makeText(getContext(), product.getMerk() + " ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();
                    });
                    binding.recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Data kosong atau gagal diambil!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                binding.swipeRefreshLayout.setRefreshing(false);
                binding.loadingAnimation.setVisibility(View.GONE);
                binding.loadingAnimation.cancelAnimation();
                binding.recyclerView.setVisibility(View.VISIBLE);

                Toast.makeText(getContext(), "Gagal mengambil data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reloadProductData() {
        RegisterAPI apiService = RetrofitClient.getRetrofitInstance().create(RegisterAPI.class);
        Call<List<Product>> call = apiService.getProducts();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList = response.body();
                    if (adapter != null) {
                        adapter.updateData(productList);
                        adapter.filter(binding.searchView.getQuery().toString(), selectedCategory);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Gagal reload produk", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterByCategory(String category) {
        selectedCategory = category;
        String searchQuery = binding.searchView.getQuery().toString();
        if (adapter != null) {
            adapter.filter(searchQuery, category);
        }
    }

    private void resetButtonStyles() {
        MaterialButton[] buttons = {
                binding.btnAll, binding.btnSelempang, binding.btnHandbag, binding.btnTotebag
        };

        for (MaterialButton button : buttons) {
            button.setStrokeWidth(2);
            button.setStrokeColorResource(R.color.colorPrimary);
            button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.transparent));
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
            button.setClickable(true);
        }
    }

    private void highlightSelectedButton(MaterialButton selectedButton) {
        selectedButton.setStrokeWidth(0);
        selectedButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary));
        selectedButton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
        selectedButton.setClickable(false);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
