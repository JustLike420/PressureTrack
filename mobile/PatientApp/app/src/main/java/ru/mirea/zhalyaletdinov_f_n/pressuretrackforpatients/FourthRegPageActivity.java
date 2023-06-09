package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients.databinding.FourthRegPageBinding;

public class FourthRegPageActivity extends AppCompatActivity {

    private FourthRegPageBinding binding;
    APIInterface apiInterface;
    String token;
    private Button regEndButton;
    private EditText snilsInput;
    private View backView;
    private ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FourthRegPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AlertDialog.Builder builder = new AlertDialog.Builder(FourthRegPageActivity.this, R.style.MyAlertDialog);
        builder.setPositiveButton("ОК", (dialog, which) -> { });

        String name = getIntent().getStringExtra("Name");
        String surname = getIntent().getStringExtra("Surname");
        String email = getIntent().getStringExtra("Email");
        String password = getIntent().getStringExtra("Password");
        float height = getIntent().getFloatExtra("Height", 176);
        float weight = getIntent().getFloatExtra("Weight", 60);
        String model = getIntent().getStringExtra("Model");

        regEndButton = binding.regEndButton;
        snilsInput = binding.snilsInput;

        regEndButton.setOnClickListener(view -> {
            if (snilsInput.getText().toString().isEmpty()) {
                builder.setTitle("Заполните СНИЛС врача");
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (snilsInput.getText().toString().replaceAll("[^0-9]", "").length() != 11) {
                builder.setTitle("Введен неправильный СНИЛС");
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                String snils = snilsInput.getText().toString().replaceAll("[^0-9]", "");
                System.out.printf("%s\n%s\n%s\n%s\n%.2f\n%.2f\n%s\n%s\n",
                        name, surname, email, password, height, weight, model, snils);
                CreatePatient.UserCreate user = new CreatePatient.UserCreate(name, surname, null, email, password);
                CreatePatient patient = new CreatePatient(user, snils, 0, height, weight, model);
                registration(patient);
            }
        });

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(14);
        snilsInput.setFilters(filters);
        snilsInput.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;
            private boolean deletingHyphen;
            private int hyphenStart;
            private boolean deletingBackward;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public synchronized void afterTextChanged(Editable s) {
                if (isFormatting)
                    return;

                isFormatting = true;

                if (deletingHyphen && hyphenStart > 0) {
                    if (deletingBackward)
                        if (hyphenStart - 1 < s.length())
                            s.delete(hyphenStart - 1, hyphenStart);
                        else if (hyphenStart < s.length())
                            s.delete(hyphenStart, hyphenStart + 1);
                }

                if (s.length() == 4 || s.length() == 8 || s.length() == 12) {
                    if (s.toString().charAt(s.length() - 1) != ' ' &&
                            s.toString().charAt(s.length() - 2) != ' ') {
                        if (s.length() == 12) {
                            s.replace(s.length() - 1, s.length(), " ");
                        } else {
                            s.insert(s.length() - 1, "-");
                        }
                    }
                }

                isFormatting = false;
            }
        });

        // Кнопка назад
        backView = binding.backView3;
        backIcon = binding.backIcon3;
        backIcon.setOnClickListener(view -> { finish(); });
        backView.setOnClickListener(view -> { finish(); });
    }

    private void registration(CreatePatient patient) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<CreatePatient> call = apiInterface.createPatient(patient);
        call.enqueue(new Callback<CreatePatient>() {
            @Override
            public void onResponse(@NonNull Call<CreatePatient> call, @NonNull Response<CreatePatient> response) {
                if (response.isSuccessful()) {AlertDialog.Builder builder = new AlertDialog.Builder(FourthRegPageActivity.this, R.style.MyAlertDialog);
                    runOnUiThread(() -> {
                        builder.setTitle("Успех!");
                        builder.setMessage("Регистрация прошла успешно.");
                        builder.setPositiveButton("Хорошо", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        Intent intent = new Intent(FourthRegPageActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else if (response.code() == 400) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(FourthRegPageActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка");
                        builder.setMessage("Не удалось зарегистрировать.");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else if (response.code() == 500) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(FourthRegPageActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Внутренняя ошибка сервера");
                        builder.setMessage("Произошла внутренняя ошибка сервера. Попробуйте позже.");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(FourthRegPageActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка сервера");
                        builder.setMessage("Произошла ошибка при обращении к серверу.");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreatePatient> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FourthRegPageActivity.this, R.style.MyAlertDialog);
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

//    private void auth(CreatePatient patient) {
//        apiInterface = APIClient.getClient().create(APIInterface.class);
//        LoginData loginData = new LoginData(patient.getUser().getEmail().trim(), patient.getUser().getPassword().trim());
//        Call<LoginResponse> call = apiInterface.performLogin(loginData);
//        call.enqueue(new Callback<LoginResponse>() {
//            @Override
//            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
//                if (response.isSuccessful()) {
//                    LoginResponse responseBody = response.body();
//                    String token = responseBody.getAuthToken();
//                    Log.d("Token", token);
//                    saveTokenToSharedPreferences(token);
//                    System.out.println(token);
//
//                    runOnUiThread(() -> {
//                        Intent mainIntent = new Intent(FourthRegPageActivity.this, MainActivity.class);
//                        startActivity(mainIntent);
//                        finish();
//                    });
//                } else if (response.code() == 400) {
//                    runOnUiThread(() -> {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(FourthRegPageActivity.this, R.style.MyAlertDialog);
//                        builder.setTitle("Неверные данные");
//                        builder.setMessage("Неправильный номер телефона/email или пароль");
//                        builder.setPositiveButton("ОК", (dialog, which) -> {});
//                        AlertDialog dialog = builder.create();
//                        dialog.show();
//                    });
//                } else if (response.code() == 500) {
//                    runOnUiThread(() -> {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(FourthRegPageActivity.this, R.style.MyAlertDialog);
//                        builder.setTitle("Внутренняя ошибка сервера");
//                        builder.setMessage("Произошла внутренняя ошибка сервера. Попробуйте позже.");
//                        builder.setPositiveButton("ОК", (dialog, which) -> {});
//                        AlertDialog dialog = builder.create();
//                        dialog.show();
//                    });
//                } else {
//                    runOnUiThread(() -> {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(FourthRegPageActivity.this, R.style.MyAlertDialog);
//                        builder.setTitle("Ошибка сервера");
//                        builder.setMessage("Произошла ошибка при обращении к серверу.");
//                        builder.setPositiveButton("ОК", (dialog, which) -> {});
//                        AlertDialog dialog = builder.create();
//                        dialog.show();
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
//                runOnUiThread(() -> {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(FourthRegPageActivity.this, R.style.MyAlertDialog);
//                    builder.setTitle("Ошибка");
//                    builder.setMessage("Не удалось выполнить операцию. Пожалуйста, проверьте подключение к сети.");
//                    builder.setPositiveButton("ОК", (dialog, which) -> {});
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//                });
//                call.cancel();
//            }
//        });
//    }
    private void saveTokenToSharedPreferences(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }
}
