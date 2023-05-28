package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients.databinding.ActivityNewTonometrBinding;

public class NewTonometrActivity extends AppCompatActivity {
    private ActivityNewTonometrBinding binding;
    APIInterface apiInterface;
    private Button newModButton;
    private View backView;
    private ImageView backIcon;
    private String device;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewTonometrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        newModButton = binding.newModButton;
        newModButton.setOnClickListener(view -> {
            if (binding.deviceET.getText().toString().isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewTonometrActivity.this, R.style.MyAlertDialog);
                builder.setTitle("Неверные данные");
                builder.setMessage("Заполните поле \"Изготовитель и модель\"");
                builder.setPositiveButton("ОК", (dialog, which) -> {});
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                apiInterface = APIClient.getClient().create(APIInterface.class);
                Device deviceObj = new Device(binding.deviceET.getText().toString().trim());
                Call<PatientProfile> call = apiInterface.changeDevice("Token " + token, deviceObj);
                call.enqueue(new Callback<PatientProfile>() {
                    @Override
                    public void onResponse(@NonNull Call<PatientProfile> call, @NonNull Response<PatientProfile> response) {
                        Log.d("Body", response.body().toString());
                        if (response.isSuccessful()) {
                            finish();
                        } else {
                            if (response.code() == 500) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(NewTonometrActivity.this, R.style.MyAlertDialog);
                                builder.setTitle("Ошибка");
                                builder.setMessage("Проблема на стороне сервера. Попробуйте чуть позже.");
                                builder.setPositiveButton("ОК", (dialog, which) -> {});
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                call.cancel();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PatientProfile> call, @NonNull Throwable t) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(NewTonometrActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка");
                        builder.setMessage("Не удалось выполнить запрос. Пожалуйста, проверьте подключение к сети и попробуйте снова.");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        call.cancel();
                    }
                });
            }
        });

        // Кнопка назад
        backView = binding.backViewT;
        backIcon = binding.backIconT;
        backIcon.setOnClickListener(view -> { finish(); });
        backView.setOnClickListener(view -> { finish(); });
    }
}