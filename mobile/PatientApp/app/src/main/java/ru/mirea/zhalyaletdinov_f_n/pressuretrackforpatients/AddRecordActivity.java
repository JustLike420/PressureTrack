package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients.databinding.ActivityAddRecordBinding;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class AddRecordActivity extends AppCompatActivity {

    private ActivityAddRecordBinding binding;
    private Button newRecButton;
    private EditText pressET;
    private View backView;
    private ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pressET = binding.pressET;
        newRecButton = binding.newRecButton;

        newRecButton.setOnClickListener(view -> {
            // Функционал
            finish();
        });

        // Кнопка назад
        backView = binding.backViewAR;
        backIcon = binding.backIconAR;
        backIcon.setOnClickListener(view -> { finish(); });
        backView.setOnClickListener(view -> { finish(); });
    }

    public void returnToMainFromRec(View view) {
        finish();
    }
}