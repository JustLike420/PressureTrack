package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import androidx.appcompat.app.AppCompatActivity;
import ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients.databinding.ActivityLoaderBinding;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LoaderActivity extends AppCompatActivity {

    private ActivityLoaderBinding binding;
    private Handler mHandler;
    private static final int LOADING_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoaderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoaderActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, LOADING_TIME_OUT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}