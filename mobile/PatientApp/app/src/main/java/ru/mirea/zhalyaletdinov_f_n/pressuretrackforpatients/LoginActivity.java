package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void loginOnButtonClick(View view) {
        TextInputEditText loginTV = findViewById(R.id.loginEmailInput);
        EditText passwordTV = findViewById(R.id.passwordLoginInput);

        if (Objects.requireNonNull(loginTV.getText()).toString().equals("user") &&
                Objects.requireNonNull(passwordTV.getText()).toString().equals("1111")) {
            Intent LoginIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(LoginIntent);
            finish();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialog);
            builder.setTitle("Неверные данные");
            builder.setMessage("Неправильный номер телефона/email или пароль");
            builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void registerOnButtonClick(View view) {
        Intent RegIntent = new Intent(LoginActivity.this,  RegistrationActivity.class);
        startActivity(RegIntent);
    }
}