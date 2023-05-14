package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NewTonometrActivity extends AppCompatActivity {

    private Button newModButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tonometr);

        newModButton = findViewById(R.id.newModButton);
        newModButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Функционал
                finish();
            }
        });
    }

    public void returnToMain(View view) {
        finish();
    }
}