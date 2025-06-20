package com.example.product_bottomnav.ui.dashboard;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.product_bottomnav.ui.product.Product;

public class OrderItem implements Parcelable {
    private String kode;
    private String merk;
    private double hargajual;
    private int quantity;
    private String foto;
    private int stok;


    // Tambahan properti baru
    private String product_merk;
    private int qty;

    // Constructor biasa
    public OrderItem(Product product) {
        this.kode = product.getKode();
        this.merk = product.getMerk();
        this.hargajual = product.getHargajual();
        this.quantity = 1;
        this.foto = product.getFoto();

        // Inisialisasi tambahan
        this.product_merk = product.getMerk();
        this.qty = 1;
    }

    // Constructor dari Parcel
    protected OrderItem(Parcel in) {
        kode = in.readString();
        merk = in.readString();
        hargajual = in.readDouble();
        quantity = in.readInt();
        foto = in.readString();

        // Baca tambahan dari Parcel
        product_merk = in.readString();
        qty = in.readInt();
    }

    public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    // Menulis data ke Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(kode);
        dest.writeString(merk);
        dest.writeDouble(hargajual);
        dest.writeInt(quantity);
        dest.writeString(foto);

        // Tambahkan ke parcel
        dest.writeString(product_merk);
        dest.writeInt(qty);
    }

    // Getter dan Setter lama
    public String getKode() {
        return kode;
    }
    public int getStok() {
        return stok;
    }
    public void setStok(int stok) {
        this.stok = stok;
    }



    public String getMerk() {
        return merk;
    }

    public double getHargajual() {
        return hargajual;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getFoto() {
        return foto;
    }

    public double getSubtotal() {
        return hargajual * quantity;
    }

    public void increaseQuantity() {
        this.quantity++;
    }

    public void decreaseQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
        }
    }

    // Getter dan Setter tambahan
    public String getProduct_merk() {
        return product_merk;
    }

    public void setProduct_merk(String product_merk) {
        this.product_merk = product_merk;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}