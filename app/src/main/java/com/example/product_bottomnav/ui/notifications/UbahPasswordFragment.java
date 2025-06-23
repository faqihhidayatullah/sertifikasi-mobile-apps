package com.example.product_bottomnav.ui.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.product_bottomnav.ApiClient;
import com.example.product_bottomnav.ApiService;
import com.example.sertifikasi.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UbahPasswordFragment extends Fragment {

    private EditText editOldPassword, editNewPassword;
    private Button btnSavePassword;
    private String email;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ubah_password, container, false);

        editOldPassword = root.findViewById(R.id.editOldPassword);
        editNewPassword = root.findViewById(R.id.editNewPassword);
        btnSavePassword = root.findViewById(R.id.btnSavePassword);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", null);

        btnSavePassword.setOnClickListener(v -> {
            String oldPassword = editOldPassword.getText().toString().trim();
            String newPassword = editNewPassword.getText().toString().trim();

            if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword)) {
                Toast.makeText(getContext(), "Semua kolom wajib diisi", Toast.LENGTH_SHORT).show();
            } else {
                updatePassword(oldPassword, newPassword);
            }
        });

        return root;
    }

    private void updatePassword(String oldPassword, String newPassword) {
        ApiService apiService = ApiClient.getService();
        Call<Void> call = apiService.updatePassword(email, oldPassword, newPassword);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Password berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                } else {
                    Toast.makeText(getContext(), "Password lama salah!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Kesalahan koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
