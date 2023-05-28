package ru.mirea.zhalyaletdinov_f_n.doctorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mirea.zhalyaletdinov_f_n.doctorapp.databinding.ActivityLoginBinding;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    APIInterface apiInterface;
    private ActivityLoginBinding binding;
    Button authButton;
    EditText passwordET,emailLoginET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        apiInterface = APIClient.getClient().create(APIInterface.class);
        authButton = binding.authButton;
        emailLoginET = binding.emailLoginET;
        passwordET = binding.passwordET;
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.MyAlertDialog);



        binding.authButton.setOnClickListener(view -> {
            LoginData loginData = new LoginData(emailLoginET.getText().toString().trim(), passwordET.getText().toString().trim());
            Call<LoginResponse> call = apiInterface.performLogin(loginData);

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        LoginResponse responseBody = response.body();
                        String token = responseBody.getAuthToken();
                        Log.d("Token", token);
                        saveTokenToSharedPreferences(token);
                        System.out.println(token);
                        Intent LoginIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(LoginIntent);
                        finish();
                    } else {
                        runOnUiThread(() -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.MyAlertDialog);
                            builder.setTitle("Неверные данные");
                            builder.setMessage("Неправильный номер телефона/email или пароль");
                            builder.setPositiveButton("ОК", (dialog, which) -> {});
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            Log.e("Response", response.toString());
                        });
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка");
                        builder.setMessage("Не удалось выполнить вход. Пожалуйста, проверьте подключение к сети и попробуйте снова.");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                    call.cancel();
                }
            });

//            if (binding.emailLoginET.getText().toString().isEmpty() ||
//                    binding.passwordET.getText().toString().isEmpty()) {
//                builder.setTitle("Ошибка");
//                builder.setMessage("Заполните все поля");
//                builder.setPositiveButton("ОК", (dialog, which) -> { });
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            }
//            else {
//                // Создать проверку данных по API в случае успеха инициализировать код внизу
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
        });
    }

    private void saveTokenToSharedPreferences(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("doctor_token", token);
        editor.apply();
    }

}