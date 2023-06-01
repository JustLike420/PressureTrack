package ru.mirea.zhalyaletdinov_f_n.doctorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import ru.mirea.zhalyaletdinov_f_n.doctorapp.databinding.ActivityChronologyBinding;

public class ChronologyActivity extends AppCompatActivity {
    ActivityChronologyBinding binding;
    APIInterface apiInterface;
    private String token, pk;
    private ImageView backImage;
    private View backView;
    private Button newTreat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChronologyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("doctor_token", "");
        System.out.println(token);

        Intent intent = getIntent();
        pk = intent.getStringExtra("PK");

        treatmentLoader(token, pk);

        newTreat = binding.newTreatmentChronoButton;
        newTreat.setOnClickListener(view -> {

        });

        backImage = binding.backImage;
        backView = binding.backView;
        backImage.setOnClickListener(view -> finish());
        backView.setOnClickListener(view -> finish());
    }

    private void treatmentLoader(String token, String pk) {

    }
}