    package com.example.product_bottomnav.ui.product;

    import android.content.Context;
    import android.content.Intent;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageButton;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.text.SpannableString;
    import android.text.TextUtils;
    import android.text.style.StrikethroughSpan;

    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.core.content.ContextCompat;
    import androidx.recyclerview.widget.RecyclerView;

    import com.bumptech.glide.Glide;
    import com.example.product_bottomnav.ApiClient;
    import com.example.product_bottomnav.DetailProductActivity;
    import com.example.product_bottomnav.R;

    import java.util.ArrayList;
    import java.util.List;

    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;

    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
        private Context context;
        private List<Product> productList;
        private List<Product> fullList;
        private OnProductClickListener listener;

        public interface OnProductClickListener {
            void onProductClick(Product product);
        }

        public ProductAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
            this.context = context;
            this.productList = productList;
            this.fullList = new ArrayList<>(productList);
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_product, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Product product = productList.get(position);

            if (product != null) {
                holder.tvMerk.setText(product.getMerk());
                holder.tvHarga.setText("Rp " + product.getHargajual());

                int stok = product.getStok();
                if (stok > 0) {
                    holder.tvStok.setText("Tersedia (" + stok + ")");
                    holder.tvStok.setTextColor(ContextCompat.getColor(context, R.color.tersedia));
                    holder.btnKeranjang.setEnabled(true);
                    holder.btnKeranjang.setAlpha(1.0f);
                } else {
                    holder.tvStok.setText("Tidak Tersedia");
                    holder.tvStok.setTextColor(ContextCompat.getColor(context, R.color.tidak_tersedia));
                    holder.btnKeranjang.setEnabled(false);
                    holder.btnKeranjang.setAlpha(0.4f);
                }

                holder.tvViewCount.setText("Dilihat: " + product.getView() + "x");

                Glide.with(context)
                        .load("https://posyandukorowelangkulon.my.id/sertifikasi/Gambar_Product/" + product.getFoto())
                        .placeholder(android.R.drawable.ic_menu_report_image)
                        .error(android.R.drawable.stat_notify_error)
                        .into(holder.imageProduct);

                holder.btnKeranjang.setOnClickListener(v -> {
                    if (listener != null && product.getStok() > 0) {
                        // Panggil listener terlebih dahulu agar produk langsung masuk keranjang
                        listener.onProductClick(product);

                        // Update stok lokal di UI


                        // Update stok di server
                        // ApiClient.getService().updateStok(product.getKode(), 1)
//     .enqueue(new Callback<Void>() {
//         @Override
//         public void onResponse(Call<Void> call, Response<Void> response) {
//             if (!response.isSuccessful()) {
//                 Toast.makeText(context, "Gagal update stok ke server", Toast.LENGTH_SHORT).show();
//             }
//         }
//
//         @Override
//         public void onFailure(Call<Void> call, Throwable t) {
//             Toast.makeText(context, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show();
//         }
//     });

                    }
                });

                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, DetailProductActivity.class);
                    intent.putExtra("produk", product);
                    context.startActivity(intent);
                });
            }
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        // Filter pencarian
        public void filter(String query, String category) {
            query = query.toLowerCase();
            category = category.toLowerCase();
            productList.clear();

            for (Product item : fullList) {
                boolean matchQuery = item.getMerk().toLowerCase().contains(query);
                boolean matchCategory = category.isEmpty() || item.getKategori().toLowerCase().equals(category);
                if (matchQuery && matchCategory) {
                    productList.add(item);
                }
            }

            notifyDataSetChanged();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvMerk, tvHarga, tvStok, tvViewCount;
            ImageView imageProduct;
            ImageButton btnKeranjang;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvMerk = itemView.findViewById(R.id.tvMerk);
                tvHarga = itemView.findViewById(R.id.tvHarga);
                tvStok = itemView.findViewById(R.id.tvStok);
                tvViewCount = itemView.findViewById(R.id.tvViewCount);
                imageProduct = itemView.findViewById(R.id.imageProduct);
                btnKeranjang = itemView.findViewById(R.id.btnKeranjang);
            }
        }
        public void updateData(List<Product> newList) {
            productList.clear();
            productList.addAll(newList);
            fullList.clear();
            fullList.addAll(newList);
            notifyDataSetChanged();
        }

    }
