package com.example.product_bottomnav;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;

    public static ApiService getService() {
        if (retrofit == null) {
            // Buat Gson yang lenient
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://posyandukorowelangkulon.my.id/sertifikasi/") // Ganti sesuai base URL kamu
                    .addConverterFactory(GsonConverterFactory.create(gson))   // Pakai gson lenient
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
