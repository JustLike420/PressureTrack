package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients.databinding.ActivityRegistrationBinding;
import ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients.databinding.SecondRegPageBinding;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SecRegPageActivity extends AppCompatActivity {

    private SecondRegPageBinding binding;
    private Button continueButton;
    private View backView;
    private ImageView backIcon;
    private EditText heightInput;
    private EditText weightInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SecondRegPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AlertDialog.Builder builder = new AlertDialog.Builder(SecRegPageActivity.this, R.style.MyAlertDialog);
        builder.setPositiveButton("ОК", (dialog, which) -> { });

        // Intent intentReg = getIntent();
        // ArrayList<String> firstPageInfo = (ArrayList<String>) intentReg.getSerializableExtra("FirstPageInfo");
        String name = getIntent().getStringExtra("Name");
        String surname = getIntent().getStringExtra("Surname");
        String email = getIntent().getStringExtra("Email");
        String password = getIntent().getStringExtra("Password");

        heightInput = binding.heightInput;
        weightInput = binding.weightInput;

        continueButton = binding.continueButton1;
        continueButton.setOnClickListener(view -> {
            if (heightInput.getText().toString().isEmpty() || weightInput.getText().toString().isEmpty()) {
                builder.setTitle("Заполните все поля");
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (!isValidHeight(Float.parseFloat(heightInput.getText().toString()))) {
                builder.setTitle("Некорректные данные в поле ввода для роста");
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (!isValidWeight(Float.parseFloat(weightInput.getText().toString()))) {
                builder.setTitle("Некорректные данные в поле ввода для веса");
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Intent intentParams = new Intent(SecRegPageActivity.this, ThirdRegPageActivity.class);
                intentParams.putExtra("Name", name);
                intentParams.putExtra("Surname", surname);
                intentParams.putExtra("Email", email);
                intentParams.putExtra("Password", password);
                intentParams.putExtra("Height", Float.parseFloat(heightInput.getText().toString()));
                intentParams.putExtra("Weight", Float.parseFloat(weightInput.getText().toString()));
                startActivity(intentParams);
            }
        });

        // Кнопка назад
        backView = binding.backView;
        backIcon = binding.backIcon;
        backIcon.setOnClickListener(view -> { finish(); });
        backView.setOnClickListener(view -> { finish(); });
    }

    public boolean isValidHeight(float height) {
        return height > 0 && height < 300;
    }

    public boolean isValidWeight(float weight) {
        return weight > 0 && weight < 1000;
    }
}
