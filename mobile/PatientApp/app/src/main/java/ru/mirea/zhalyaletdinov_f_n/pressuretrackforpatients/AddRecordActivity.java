package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddRecordActivity extends AppCompatActivity {

    private Button newRecButton;
    private EditText pressET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        pressET = findViewById(R.id.pressET);
        newRecButton = findViewById(R.id.newRecButton);

        newRecButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Функционал
                finish();
            }
        });
    }

    public void returnToMainFromRec(View view) {
        finish();
    }
}