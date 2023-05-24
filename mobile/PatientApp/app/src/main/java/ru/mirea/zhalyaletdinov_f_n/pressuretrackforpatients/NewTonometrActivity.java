package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients.databinding.ActivityNewTonometrBinding;

public class NewTonometrActivity extends AppCompatActivity {

    private ActivityNewTonometrBinding binding;
    private Button newModButton;
    private View backView;
    private ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewTonometrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        newModButton = binding.newModButton;
        newModButton.setOnClickListener(view -> {
            // Функционал
            finish();
        });

        // Кнопка назад
        backView = binding.backViewT;
        backIcon = binding.backIconT;
        backIcon.setOnClickListener(view -> { finish(); });
        backView.setOnClickListener(view -> { finish(); });
    }
}