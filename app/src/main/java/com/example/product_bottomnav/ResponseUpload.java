package com.example.product_bottomnav;

import com.google.gson.annotations.SerializedName;

public class ResponseUpload {

    @SerializedName("kode") String kode;

    @SerializedName("pesan") String pesan;

    public String getKode() { return kode; }
    public void setKode (String kode) { this.kode = kode; }

    public String getPesan () { return pesan; }
    public void setPesan (String pesan) { this.pesan = pesan; }
}

