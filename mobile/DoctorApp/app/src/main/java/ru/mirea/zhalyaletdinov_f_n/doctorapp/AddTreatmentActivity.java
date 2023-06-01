package ru.mirea.zhalyaletdinov_f_n.doctorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mirea.zhalyaletdinov_f_n.doctorapp.databinding.ActivityAddTreatmentBinding;
import ru.mirea.zhalyaletdinov_f_n.doctorapp.databinding.ActivityChronologyBinding;

public class AddTreatmentActivity extends AppCompatActivity {

    ActivityAddTreatmentBinding binding;
    APIInterface apiInterface;
    private String token, pk;
    private ImageView backImage;
    private View backView;
    private Button postNewTreatmentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTreatmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("doctor_token", "");

        Intent intent = getIntent();
        pk = intent.getStringExtra("PK");

        postNewTreatmentButton = binding.postNewTreatmentButton;
        postNewTreatmentButton.setOnClickListener(view -> {
            if (binding.editTextTextPersonName.getText().toString().isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddTreatmentActivity.this, R.style.MyAlertDialog);
                builder.setTitle("Ошибка");
                builder.setMessage("Вы не можете отправить пустое поле!");
                builder.setPositiveButton("ОК", (dialog, which) -> {});
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                postTreatment(token, pk, binding.editTextTextPersonName.getText().toString().trim());
            }
        });

        backImage = binding.backImage;
        backView = binding.backView;
        backImage.setOnClickListener(view -> finish());
        backView.setOnClickListener(view -> finish());
    }

    private void clearToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("doctor_token", "");
        editor.apply();
    }

    private void postTreatment(String token, String pk, String mess) {
        TreatmentData treatmentData = new TreatmentData(mess);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<Void> call = apiInterface.createTreatment("Token " + token, pk, treatmentData);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddTreatmentActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Лечение назначено");
                        builder.setMessage("Данные успешно отправлены пациенту.");
                        builder.setPositiveButton("Закрыть", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        binding.editTextTextPersonName.setText("");
                    });
                } else if (response.code() == 400) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddTreatmentActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка");
                        builder.setMessage("Не удалось получить данные о измерениях пациента!");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else if (response.code() == 401) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddTreatmentActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка аутентификации");
                        builder.setMessage("Неправильный токен аутентификации");
                        builder.setPositiveButton("ОК", (dialog, which) -> {
                            clearToken();
                            Intent intent = new Intent(AddTreatmentActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else if (response.code() == 500) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddTreatmentActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Внутренняя ошибка сервера");
                        builder.setMessage("Произошла внутренняя ошибка сервера. Попробуйте позже.");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddTreatmentActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка сервера");
                        builder.setMessage("Произошла ошибка при обращении к серверу.");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddTreatmentActivity.this, R.style.MyAlertDialog);
                    builder.setTitle("Ошибка");
                    builder.setMessage("Не удалось выполнить операцию. Пожалуйста, проверьте подключение к сети.");
                    builder.setPositiveButton("ОК", (dialog, which) -> {});
                    AlertDialog dialog = builder.create();
                    dialog.show();
                });
                call.cancel();
            }
        });
    }
}