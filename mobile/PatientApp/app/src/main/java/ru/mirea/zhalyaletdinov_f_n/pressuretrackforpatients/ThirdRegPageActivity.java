package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ThirdRegPageActivity extends AppCompatActivity {

    private Button continueButton2;
    private EditText modelET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third_reg_page);

        AlertDialog.Builder builder = new AlertDialog.Builder(ThirdRegPageActivity.this, R.style.MyAlertDialog);
        builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });

        String name = getIntent().getStringExtra("Name");
        String surname = getIntent().getStringExtra("Surname");
        String email = getIntent().getStringExtra("Email");
        String password = getIntent().getStringExtra("Password");
        Float height = getIntent().getFloatExtra("Height", 176);
        Float weight = getIntent().getFloatExtra("Weight", 60);

        modelET = findViewById(R.id.modelET);
        continueButton2 = findViewById(R.id.continueButton2);
        continueButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
    }


    public void GetBackOnClick2(View view) {
        finish();
    }
}
