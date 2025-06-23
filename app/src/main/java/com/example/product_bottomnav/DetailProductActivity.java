package com.example.product_bottomnav;
import com.example.sertifikasi.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.product_bottomnav.ui.dashboard.OrderHelper;
import com.example.product_bottomnav.ui.product.Product;
import com.example.product_bottomnav.ui.product.ViewResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class DetailProductActivity extends AppCompatActivity {

    ImageSlider imageSlider;
    TextView tvMerk, tvHarga, tvStok, tvDeskripsi, tvKategori, tvViewCount;
    Button btnBeli;
    Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);

        imageSlider = findViewById(R.id.imageSlider);
        tvMerk = findViewById(R.id.tvDetailMerk);
        tvHarga = findViewById(R.id.tvDetailHarga);
        tvStok = findViewById(R.id.tvDetailStok);
        tvDeskripsi = findViewById(R.id.tvDetailDeskripsi);
        tvKategori = findViewById(R.id.tvDetailKategori);
        tvViewCount = findViewById(R.id.tvDetailViewCount);
        btnBeli = findViewById(R.id.btnBeli);

        product = (Product) getIntent().getSerializableExtra("produk");

        if (product != null) {
            updateAndFetchView(product.getKode());

            tvMerk.setText(product.getMerk());
            tvHarga.setText(String.format("Rp %,d", (int) product.getHargajual()));
            tvStok.setText(product.getStok() > 0 ? "Tersedia (" + product.getStok() + ")" : "Tidak tersedia");
            tvDeskripsi.setText(product.getDeskripsi());
            tvKategori.setText("Kategori: " + (product.getKategori() != null ? product.getKategori() : "-"));

            btnBeli.setOnClickListener(v -> {
                if (product.getStok() > 0) {
                    SharedPreferences userPrefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
                    String email = userPrefs.getString("email", "guest@example.com");

                    OrderHelper orderHelper = new OrderHelper(this, email);
                    orderHelper.addToOrder(product); // tanpa ukuran

                    Toast.makeText(this, product.getMerk() + " ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();
                    finish(); // kembali ke activity sebelumnya
                } else {
                    Toast.makeText(this, "Stok produk habis!", Toast.LENGTH_SHORT).show();
                }
            });


            loadProductImages(product.getKode());
        }
    }

    private void updateAndFetchView(String kode) {
        ApiClient.getService().tambahView(kode).enqueue(new Callback<ViewResponse>() {
            @Override
            public void onResponse(Call<ViewResponse> call, Response<ViewResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int updatedViewCount = response.body().getView();
                    tvViewCount.setText("Dilihat: " + updatedViewCount + "x");
                }
            }
            @Override
            public void onFailure(Call<ViewResponse> call, Throwable t) {}
        });
    }

    private void loadProductImages(String kode) {
        ApiClient.getService().getProductImages(kode).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> fotoNames = response.body();
                    List<SlideModel> slideModels = new ArrayList<>();
                    for (String fotoName : fotoNames) {
                        String fullUrl = "https://posyandukorowelangkulon.my.id/sertifikasi/Gambar_Product/" + fotoName;
                        slideModels.add(new SlideModel(fullUrl, ScaleTypes.FIT));
                    }
                    imageSlider.setImageList(slideModels, ScaleTypes.FIT);
                }
            }
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {}
        });
    }
}
