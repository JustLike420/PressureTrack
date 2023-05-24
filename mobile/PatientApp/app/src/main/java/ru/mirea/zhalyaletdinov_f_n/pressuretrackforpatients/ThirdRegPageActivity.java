package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients.databinding.ThirdRegPageBinding;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ThirdRegPageActivity extends AppCompatActivity {

    private ThirdRegPageBinding binding;
    private Button continueButton;
    private EditText modelET;
    private View backView;
    private ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ThirdRegPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AlertDialog.Builder builder = new AlertDialog.Builder(ThirdRegPageActivity.this, R.style.MyAlertDialog);
        builder.setPositiveButton("ОК", (dialog, which) -> { });

        String name = getIntent().getStringExtra("Name");
        String surname = getIntent().getStringExtra("Surname");
        String email = getIntent().getStringExtra("Email");
        String password = getIntent().getStringExtra("Password");
        Float height = getIntent().getFloatExtra("Height", 176);
        Float weight = getIntent().getFloatExtra("Weight", 60);

        modelET = binding.modelET;
        continueButton = binding.continueButton2;
        continueButton.setOnClickListener(view -> {
            if (modelET.getText().toString().isEmpty()) {
                builder.setTitle("Заполните поле с названием и производителем тонометра");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                Intent intentModel = new Intent(ThirdRegPageActivity.this, FourthRegPageActivity.class);
                intentModel.putExtra("Name", name);
                intentModel.putExtra("Surname", surname);
                intentModel.putExtra("Email", email);
                intentModel.putExtra("Password", password);
                intentModel.putExtra("Height", height);
                intentModel.putExtra("Weight", weight);
                intentModel.putExtra("Model", modelET.getText().toString());
                startActivity(intentModel);
            }
        });

        // Кнопка назад
        backView = binding.backView2;
        backIcon = binding.backIcon2;
        backIcon.setOnClickListener(view -> { finish(); });
        backView.setOnClickListener(view -> { finish(); });
    }
}
