package com.example.product_bottomnav;

import static androidx.core.content.ContentProviderCompat.requireContext;
import com.example.sertifikasi.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.product_bottomnav.ui.dashboard.OrderHelper;
import com.example.product_bottomnav.ui.dashboard.OrderManager;
import com.example.product_bottomnav.ui.product.Product;
import com.example.product_bottomnav.ui.product.ProductAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductByKategoriActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_by_kategori);

        recyclerView = findViewById(R.id.recyclerViewProduct);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        adapter = new ProductAdapter(this, productList, this); // Listener diterapkan
        recyclerView.setAdapter(adapter);

        String kategori = getIntent().getStringExtra("kategori");
        if (kategori != null) {
            getProdukByKategori(kategori);
        } else {
            Toast.makeText(this, "Kategori tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    private void getProdukByKategori(String kategori) {
        String url = "https://posyandukorowelangkulon.my.id/sertifikasi/product_by_kategori.php?kategori=" + kategori;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        productList.clear();
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Product product = new Product(
                                    obj.getString("kode"),
                                    obj.getString("merk"),
                                    obj.getDouble("hargajual"),
                                    obj.getInt("stok"),
                                    obj.getString("foto"),
                                    obj.getString("deskripsi"),
                                    obj.getString("kategori"),
                                    obj.getInt("view")
                            );
                            productList.add(product);
                        }
                        adapter.notifyDataSetChanged(); // Memberitahu adapter bahwa data berubah
                    } catch (JSONException e) {
                        Toast.makeText(this, "Gagal parsing data produk", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal load produk dari server", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    // Ini dipanggil ketika tombol keranjang ditekan di adapter
    @Override
    public void onProductClick(Product product) {
        // Ambil email user dari SharedPreferences (pastikan nama shared prefs dan key sesuai di app kamu)
        SharedPreferences userPrefs = this.getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        String email = userPrefs.getString("email", "guest@example.com");

        // Buat OrderHelper dengan email user yang sedang login
        OrderHelper orderHelper = new OrderHelper(this, email);

        // Tambah produk ke order sesuai user
        orderHelper.addToOrder(product);

        Toast.makeText(this, product.getMerk() + " ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();
    }

}
