package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    public void NextStepOnButtonClick(View view) {
        EditText fioTV = findViewById(R.id.fioInput);
        EditText emailTV = findViewById(R.id.emailInput);
        EditText passwordTV = findViewById(R.id.passwordRegInput);

        if (fioTV.getText().toString().equals("") || emailTV.getText().toString().equals("") ||
                passwordTV.getText().toString().equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialog);
            builder.setTitle("Заполните все поля!!!");
            builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            setContentView(R.layout.second_reg_page);
//            EditText height = findViewById(R.id.heightInput);
//            EditText weight = findViewById(R.id.weightInput);
//            Button button = findViewById(R.id.continueButton1);
//
//            TextWatcher textWatcher = new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
//                    button.setEnabled(!height.getText().toString().isEmpty() && !weight.getText().toString().isEmpty());
//                }
//            };
        }
    }

    public void AuthPageOnButtonClick(View view) {
        finish();
    }

    public void GetBackOnClick1(View view) {
        setContentView(R.layout.activity_registration);
    }

    public void goToNextPage2Button(View view) {
        setContentView(R.layout.third_reg_page);
    }

    public void GetBackOnClick2(View view) {
        setContentView(R.layout.second_reg_page);
    }

    public void goToNextPage3Button(View view) {
        setContentView(R.layout.fourth_reg_page);
    }

    public void GetBackOnClick3(View view) {
        setContentView(R.layout.third_reg_page);
    }
}