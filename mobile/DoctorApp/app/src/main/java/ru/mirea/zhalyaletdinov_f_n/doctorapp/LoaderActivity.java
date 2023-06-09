package ru.mirea.zhalyaletdinov_f_n.doctorapp;

import androidx.appcompat.app.AppCompatActivity;
import ru.mirea.zhalyaletdinov_f_n.doctorapp.databinding.ActivityLoaderBinding;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class LoaderActivity extends AppCompatActivity {

    private Handler mHandler;
    private static final int LOADING_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        mHandler = new Handler();
        mHandler.postDelayed(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("doctor_token", "");

            Intent intent;
            if (!token.isEmpty()) {
                intent = new Intent(LoaderActivity.this, MainActivity.class);
            } else {
                intent = new Intent(LoaderActivity.this, LoginActivity.class);
            }
            startActivity(intent);
        }, LOADING_TIME_OUT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}