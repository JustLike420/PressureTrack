package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    APIInterface apiInterface;
    private ActivityLoginBinding binding;
     Button loginButton, registerButton;
     TextInputEditText loginTV;
     EditText passwordTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiInterface = APIClient.getClient().create(APIInterface.class);

        loginButton = binding.loginButton;
        registerButton = binding.regButton;

        loginTV = binding.loginEmailInput;
        passwordTV = binding.passwordLoginInput;

        loginButton.setOnClickListener(view -> {
            LoginData loginData = new LoginData(loginTV.getText().toString().trim(), passwordTV.getText().toString().trim());
            Call<LoginResponse> call = apiInterface.performLogin(loginData);

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        LoginResponse responseBody = response.body();
                        System.out.println(response.body().toString());
                        String user_role = response.body().getUserRole();
                        if (!user_role.equals("patient")) {
                            runOnUiThread(() -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.MyAlertDialog);
                                builder.setTitle("Неверные данные");
                                builder.setMessage("Неправильный номер телефона/email или пароль");
                                builder.setPositiveButton("ОК", (dialog, which) -> {});
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            });
                        } else {
                            String token = responseBody.getAuthToken();
                            Log.d("Token", token);
                            saveTokenToSharedPreferences(token);
                            runOnUiThread(() -> {
                                Intent LoginIntent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(LoginIntent);
                                finish();
                            });
                        }
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
        });

        registerButton.setOnClickListener(view -> {
            Intent RegIntent = new Intent(LoginActivity.this,  RegistrationActivity.class);
            startActivity(RegIntent);
        });
    }

    private void saveTokenToSharedPreferences(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }
}