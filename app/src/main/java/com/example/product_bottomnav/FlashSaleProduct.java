package com.example.product_bottomnav;
public class FlashSaleProduct {
    private String nama;
    private Double hargaAsli;
    private Double hargaDiskon;
    private String imageUrl;

    public FlashSaleProduct(String nama, Double hargaAsli, Double hargaDiskon, String imageUrl) {
        this.nama = nama;
        this.hargaAsli = hargaAsli;
        this.hargaDiskon = hargaDiskon;
        this.imageUrl = imageUrl;
    }


    public String getNama() {
        return nama;
    }

    public Double getHargaAsli() {
        return hargaAsli;
    }

    public Double getHargaDiskon() {
        return hargaDiskon;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
