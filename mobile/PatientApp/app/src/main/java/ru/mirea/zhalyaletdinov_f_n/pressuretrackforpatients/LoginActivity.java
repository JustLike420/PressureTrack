package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients.databinding.ActivityLoginBinding;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    Button loginButton, registerButton;
    TextInputEditText loginTV;
    EditText passwordTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.MyAlertDialog);

        loginButton = binding.loginButton;
        registerButton = binding.regButton;

        loginTV = binding.loginEmailInput;
        passwordTV = binding.passwordLoginInput;
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginTV.getText().toString().isEmpty() || passwordTV.getText().toString().isEmpty()) {
                    builder.setTitle("Неверные данные");
                    builder.setMessage("Неправильный номер телефона/email или пароль");
                    builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Intent LoginIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(LoginIntent);
                    finish();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent RegIntent = new Intent(LoginActivity.this,  RegistrationActivity.class);
                startActivity(RegIntent);
            }
        });
    }
}