package com.example.product_bottomnav;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.example.sertifikasi.R;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private View colorLeft, colorRight, logoContainer, whiteBackground;
    private static final int SPLASH_DURATION = 5000; // 5 detik
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Inisialisasi view
        colorLeft = findViewById(R.id.colorLeft);
        colorRight = findViewById(R.id.colorRight);
        logoContainer = findViewById(R.id.logoContainer);
        whiteBackground = findViewById(R.id.whiteBackground);

        // Set kondisi awal
        setupInitialStates();

        // Mulai animasi splash
        startSplashAnimation();
    }

    private void setupInitialStates() {
        // Buat overlay hitam untuk fade in awal
        View rootView = findViewById(android.R.id.content);
        rootView.setAlpha(0f);

        // Sembunyikan semua view pada awalnya
        colorLeft.setVisibility(View.INVISIBLE);
        colorRight.setVisibility(View.INVISIBLE);
        logoContainer.setVisibility(View.INVISIBLE);
        whiteBackground.setVisibility(View.INVISIBLE);

        // Set nilai alpha dan scale awal
        colorLeft.setAlpha(0f);
        colorRight.setAlpha(0f);
        logoContainer.setAlpha(0f);
        whiteBackground.setAlpha(0f);

        logoContainer.setScaleX(0f);
        logoContainer.setScaleY(0f);
        whiteBackground.setScaleX(0f);
        whiteBackground.setScaleY(0f);

        // Reset rotasi
        colorLeft.setRotation(0f);
        colorRight.setRotation(0f);
    }

    private void startSplashAnimation() {
        // Fase 1: Fade in dari layar hitam (0-0.8 detik)
        fadeInFromBlack();

        // Fase 2: Tampilkan dan perluas warna (0.8-2.5 detik)
        handler.postDelayed(this::showAndExpandColors, 800);

        // Fase 3: Miringkan warna saat bertemu (2.5-3 detik)
        handler.postDelayed(this::tiltColors, 2500);

        // Fase 4: Tampilkan background putih + logo dengan efek zoom (3-4.2 detik)
        handler.postDelayed(this::showWhiteBackgroundAndLogo, 3000);

        // Fase 5: Persiapan transisi keluar (4.5 detik)
        handler.postDelayed(this::prepareExitTransition, 4500);

        // Fase 6: Navigasi ke aktivitas utama (setelah 5 detik)
        handler.postDelayed(this::navigateToMainActivity, SPLASH_DURATION);
    }

    private void fadeInFromBlack() {
        // Fade in seluruh layar dari hitam
        View rootView = findViewById(android.R.id.content);
        ObjectAnimator screenFadeIn = ObjectAnimator.ofFloat(rootView, "alpha", 0f, 1f);
        screenFadeIn.setDuration(800);
        screenFadeIn.setInterpolator(new AccelerateDecelerateInterpolator());
        screenFadeIn.start();
    }

    private void showAndExpandColors() {
        // Tampilkan view warna dengan fade in
        colorLeft.setVisibility(View.VISIBLE);
        colorRight.setVisibility(View.VISIBLE);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        // Perbesar lebar pertemuan untuk memastikan coverage yang lebih baik saat dimiringkan
        int meetingWidth = (int) (screenWidth * 0.75f); // Ditingkatkan dari 0.6f ke 0.75f

        // Fade in dan perluas warna kiri secara bersamaan
        ObjectAnimator fadeInLeft = ObjectAnimator.ofFloat(colorLeft, "alpha", 0f, 1f);
        ValueAnimator expandLeft = ValueAnimator.ofInt(0, meetingWidth);
        expandLeft.addUpdateListener(animation -> {
            int width = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams params = colorLeft.getLayoutParams();
            params.width = width;
            colorLeft.setLayoutParams(params);
        });

        // Fade in dan perluas warna kanan secara bersamaan
        ObjectAnimator fadeInRight = ObjectAnimator.ofFloat(colorRight, "alpha", 0f, 1f);
        ValueAnimator expandRight = ValueAnimator.ofInt(0, meetingWidth);
        expandRight.addUpdateListener(animation -> {
            int width = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams params = colorRight.getLayoutParams();
            params.width = width;
            colorRight.setLayoutParams(params);
        });

        // Set durasi dan interpolator
        fadeInLeft.setDuration(600);
        fadeInRight.setDuration(600);
        expandLeft.setDuration(1700);
        expandRight.setDuration(1700);

        fadeInLeft.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeInRight.setInterpolator(new AccelerateDecelerateInterpolator());
        expandLeft.setInterpolator(new AccelerateDecelerateInterpolator());
        expandRight.setInterpolator(new AccelerateDecelerateInterpolator());

        // Mulai semua animasi
        fadeInLeft.start();
        fadeInRight.start();
        expandLeft.start();
        expandRight.start();
    }

    private void tiltColors() {
        // Dapatkan dimensi layar untuk scaling yang tepat
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Hitung skala tambahan yang diperlukan untuk mengisi celah saat dirotasi
        // Rumus: sqrt(2) untuk rotasi 45°, tapi kita gunakan lebih besar untuk safety
        float extraScale = 1.8f; // Ditingkatkan dari 1.2f ke 1.8f untuk coverage penuh

        // Buat animasi kemiringan dengan scaling yang tepat
        ObjectAnimator tiltLeft = ObjectAnimator.ofFloat(colorLeft, "rotation", 0f, -15f);
        tiltLeft.setDuration(400);
        tiltLeft.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator tiltRight = ObjectAnimator.ofFloat(colorRight, "rotation", 0f, 15f);
        tiltRight.setDuration(400);
        tiltRight.setInterpolator(new AccelerateDecelerateInterpolator());

        // Scale up kedua view untuk menghilangkan celah putih saat dimiringkan
        ObjectAnimator scaleLeftX = ObjectAnimator.ofFloat(colorLeft, "scaleX", 1f, extraScale);
        ObjectAnimator scaleLeftY = ObjectAnimator.ofFloat(colorLeft, "scaleY", 1f, extraScale);
        ObjectAnimator scaleRightX = ObjectAnimator.ofFloat(colorRight, "scaleX", 1f, extraScale);
        ObjectAnimator scaleRightY = ObjectAnimator.ofFloat(colorRight, "scaleY", 1f, extraScale);

        scaleLeftX.setDuration(400);
        scaleLeftY.setDuration(400);
        scaleRightX.setDuration(400);
        scaleRightY.setDuration(400);

        scaleLeftX.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleLeftY.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleRightX.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleRightY.setInterpolator(new AccelerateDecelerateInterpolator());

        // Penyesuaian posisi yang lebih besar untuk memastikan coverage penuh
        float translationDistance = screenWidth * 0.1f; // 10% dari lebar layar
        ObjectAnimator translateLeftX = ObjectAnimator.ofFloat(colorLeft, "translationX", 0f, -translationDistance);
        ObjectAnimator translateRightX = ObjectAnimator.ofFloat(colorRight, "translationX", 0f, translationDistance);

        // Tambahkan translasi Y untuk coverage yang lebih baik
        float translationY = screenHeight * 0.05f; // 5% dari tinggi layar
        ObjectAnimator translateLeftY = ObjectAnimator.ofFloat(colorLeft, "translationY", 0f, -translationY);
        ObjectAnimator translateRightY = ObjectAnimator.ofFloat(colorRight, "translationY", 0f, translationY);

        translateLeftX.setDuration(400);
        translateRightX.setDuration(400);
        translateLeftY.setDuration(400);
        translateRightY.setDuration(400);

        translateLeftX.setInterpolator(new AccelerateDecelerateInterpolator());
        translateRightX.setInterpolator(new AccelerateDecelerateInterpolator());
        translateLeftY.setInterpolator(new AccelerateDecelerateInterpolator());
        translateRightY.setInterpolator(new AccelerateDecelerateInterpolator());

        // Buat animator set untuk kemiringan tersinkronisasi dengan coverage yang tepat
        AnimatorSet tiltSet = new AnimatorSet();
        tiltSet.playTogether(
                tiltLeft, tiltRight,
                scaleLeftX, scaleLeftY, scaleRightX, scaleRightY,
                translateLeftX, translateRightX, translateLeftY, translateRightY
        );
        tiltSet.start();
    }

    private void showWhiteBackgroundAndLogo() {
        // Tampilkan background putih dan logo
        whiteBackground.setVisibility(View.VISIBLE);
        logoContainer.setVisibility(View.VISIBLE);

        // Fase 1: Background putih zoom in dari kecil ke layar penuh
        ObjectAnimator whiteBgScaleX = ObjectAnimator.ofFloat(whiteBackground, "scaleX", 0f, 1f);
        ObjectAnimator whiteBgScaleY = ObjectAnimator.ofFloat(whiteBackground, "scaleY", 0f, 1f);
        ObjectAnimator whiteBgAlpha = ObjectAnimator.ofFloat(whiteBackground, "alpha", 0f, 1f);

        whiteBgScaleX.setDuration(500);
        whiteBgScaleY.setDuration(500);
        whiteBgAlpha.setDuration(300);

        whiteBgScaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        whiteBgScaleY.setInterpolator(new AccelerateDecelerateInterpolator());
        whiteBgAlpha.setInterpolator(new AccelerateDecelerateInterpolator());

        // Set animasi background putih
        AnimatorSet whiteBgSet = new AnimatorSet();
        whiteBgSet.playTogether(whiteBgScaleX, whiteBgScaleY, whiteBgAlpha);

        // Fase 2: Logo zoom in setelah background putih muncul
        ObjectAnimator logoScaleX1 = ObjectAnimator.ofFloat(logoContainer, "scaleX", 0f, 1.3f);
        ObjectAnimator logoScaleY1 = ObjectAnimator.ofFloat(logoContainer, "scaleY", 0f, 1.3f);
        ObjectAnimator logoAlpha1 = ObjectAnimator.ofFloat(logoContainer, "alpha", 0f, 1f);

        logoScaleX1.setDuration(600);
        logoScaleY1.setDuration(600);
        logoAlpha1.setDuration(400);

        logoScaleX1.setInterpolator(new OvershootInterpolator(1.2f));
        logoScaleY1.setInterpolator(new OvershootInterpolator(1.2f));
        logoAlpha1.setInterpolator(new AccelerateDecelerateInterpolator());

        // Fase 3: Logo kembali ke ukuran normal
        ObjectAnimator logoScaleX2 = ObjectAnimator.ofFloat(logoContainer, "scaleX", 1.3f, 1f);
        ObjectAnimator logoScaleY2 = ObjectAnimator.ofFloat(logoContainer, "scaleY", 1.3f, 1f);
        logoScaleX2.setDuration(300);
        logoScaleY2.setDuration(300);
        logoScaleX2.setInterpolator(new BounceInterpolator());
        logoScaleY2.setInterpolator(new BounceInterpolator());

        // Set logo zoom in
        AnimatorSet logoZoomInSet = new AnimatorSet();
        logoZoomInSet.playTogether(logoScaleX1, logoScaleY1, logoAlpha1);

        // Set logo kembali normal
        AnimatorSet logoSettleSet = new AnimatorSet();
        logoSettleSet.playTogether(logoScaleX2, logoScaleY2);

        // Animasi logo lengkap
        AnimatorSet logoCompleteSet = new AnimatorSet();
        logoCompleteSet.playSequentially(logoZoomInSet, logoSettleSet);

        // Rangkai background putih kemudian logo
        AnimatorSet completeSet = new AnimatorSet();
        completeSet.playSequentially(whiteBgSet, logoCompleteSet);

        completeSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Tambahkan efek glow halus ke logo
                addLogoGlowEffect();
            }
        });

        completeSet.start();
    }

    private void addLogoGlowEffect() {
        // Efek pulse halus untuk logo
        ObjectAnimator pulse1 = ObjectAnimator.ofFloat(logoContainer, "scaleX", 1f, 1.05f, 1f);
        ObjectAnimator pulse2 = ObjectAnimator.ofFloat(logoContainer, "scaleY", 1f, 1.05f, 1f);
        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(logoContainer, "alpha", 1f, 0.8f, 1f);

        pulse1.setDuration(1000);
        pulse2.setDuration(1000);
        alpha1.setDuration(1000);

        pulse1.setInterpolator(new AccelerateDecelerateInterpolator());
        pulse2.setInterpolator(new AccelerateDecelerateInterpolator());
        alpha1.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet pulseSet = new AnimatorSet();
        pulseSet.playTogether(pulse1, pulse2, alpha1);
        pulseSet.start();
    }

    private void prepareExitTransition() {
        // Mulai fade out warna dengan halus
        fadeOutColors();

        // Tambahkan efek zoom out halus ke logo dan background putih
        ObjectAnimator logoZoomOut = ObjectAnimator.ofFloat(logoContainer, "scaleX", 1f, 0.95f);
        ObjectAnimator logoZoomOutY = ObjectAnimator.ofFloat(logoContainer, "scaleY", 1f, 0.95f);
        ObjectAnimator logoFadeOut = ObjectAnimator.ofFloat(logoContainer, "alpha", 1f, 0.7f);

        ObjectAnimator whiteBgFadeOut = ObjectAnimator.ofFloat(whiteBackground, "alpha", 1f, 0.8f);

        logoZoomOut.setDuration(500);
        logoZoomOutY.setDuration(500);
        logoFadeOut.setDuration(500);
        whiteBgFadeOut.setDuration(500);

        AnimatorSet exitPrepSet = new AnimatorSet();
        exitPrepSet.playTogether(logoZoomOut, logoZoomOutY, logoFadeOut, whiteBgFadeOut);
        exitPrepSet.start();
    }

    private void fadeOutColors() {
        // Fade out warna secara bertahap dengan efek stagger
        ObjectAnimator fadeOutLeft = ObjectAnimator.ofFloat(colorLeft, "alpha", 1f, 0f);
        ObjectAnimator fadeOutRight = ObjectAnimator.ofFloat(colorRight, "alpha", 1f, 0f);

        // Stagger fade out - kiri dulu, kemudian kanan
        fadeOutLeft.setDuration(600);
        fadeOutRight.setDuration(600);
        fadeOutRight.setStartDelay(100); // Delay sedikit untuk efek stagger

        fadeOutLeft.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeOutRight.setInterpolator(new AccelerateDecelerateInterpolator());

        fadeOutLeft.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                colorLeft.setVisibility(View.GONE);
            }
        });

        fadeOutRight.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                colorRight.setVisibility(View.GONE);
            }
        });

        fadeOutLeft.start();
        fadeOutRight.start();
    }

    private void navigateToMainActivity() {
        SharedPreferences prefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        boolean isGuest = prefs.getBoolean("isGuest", false);

        // Jika belum login, set login sebagai tamu
        if (!isLoggedIn) {
            editor.putBoolean("isLoggedIn", true);
            editor.putBoolean("isGuest", true);
            editor.putString("userType", "guest");
            editor.putString("email", "guest@guest.com");
            editor.putString("nama", "Guest User");
            editor.apply();
        }

        // Fade out final ke hitam sebelum transisi
        View rootView = findViewById(android.R.id.content);
        ObjectAnimator finalFadeOut = ObjectAnimator.ofFloat(rootView, "alpha", 1f, 0f);
        finalFadeOut.setDuration(400);
        finalFadeOut.setInterpolator(new AccelerateDecelerateInterpolator());

        finalFadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Navigasi ke MainActivity dengan transisi kustom
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);

                // Gunakan transisi fade yang halus
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        finalFadeOut.start();
    }

    @Override
    public void onBackPressed() {
        // Nonaktifkan tombol back selama splash
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Bersihkan callback handler untuk mencegah memory leak
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}