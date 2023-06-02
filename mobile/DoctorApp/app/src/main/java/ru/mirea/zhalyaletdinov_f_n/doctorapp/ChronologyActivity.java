package ru.mirea.zhalyaletdinov_f_n.doctorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mirea.zhalyaletdinov_f_n.doctorapp.databinding.ActivityChronologyBinding;

public class ChronologyActivity extends AppCompatActivity {
    ActivityChronologyBinding binding;
    APIInterface apiInterface;
    private String token, pk;
    private ImageView backImage;
    private View backView;
    private Button newTreat;
    private RecyclerView treatRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChronologyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("doctor_token", "");

        Intent intent = getIntent();
        pk = intent.getStringExtra("PK");

        treatmentLoader(token, pk);

        newTreat = binding.newTreatmentChronoButton;
        newTreat.setOnClickListener(view -> {
            Intent intentNewTreatment = new Intent(ChronologyActivity.this, AddTreatmentActivity.class);
            intentNewTreatment.putExtra("PK", pk);
            startActivity(intentNewTreatment);
        });

        backImage = binding.backImage;
        backView = binding.backView;
        backImage.setOnClickListener(view -> finish());
        backView.setOnClickListener(view -> finish());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        treatmentLoader(token, pk);
    }

    @Override
    protected void onResume() {
        super.onResume();
        treatmentLoader(token, pk);
    }

    private void clearToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("doctor_token", "");
        editor.apply();
    }

    private void treatmentLoader(String token, String pk) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<TreatmentData>> call = apiInterface.getTreatments("Token " + token, pk);
        call.enqueue(new Callback<List<TreatmentData>>() {
            @Override
            public void onResponse(@NonNull Call<List<TreatmentData>> call, @NonNull Response<List<TreatmentData>> response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        List<TreatmentData> treatmentData = response.body();
                        assert treatmentData != null;
                        Collections.reverse(treatmentData);
                        initTreatmentRV(treatmentData);
                    });
                } else if (response.code() == 400) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChronologyActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка");
                        builder.setMessage("Не удалось получить данные о измерениях пациента!");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else if (response.code() == 401) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChronologyActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка аутентификации");
                        builder.setMessage("Неправильный токен аутентификации");
                        builder.setPositiveButton("ОК", (dialog, which) -> {
                            clearToken();
                            Intent intent = new Intent(ChronologyActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else if (response.code() == 500) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChronologyActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Внутренняя ошибка сервера");
                        builder.setMessage("Произошла внутренняя ошибка сервера. Попробуйте позже.");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChronologyActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка сервера");
                        builder.setMessage("Произошла ошибка при обращении к серверу.");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TreatmentData>> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChronologyActivity.this, R.style.MyAlertDialog);
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

    private void initTreatmentRV(List<TreatmentData> treatmentData) {
        treatRV = binding.treatRV;
        treatRV.setLayoutManager(new LinearLayoutManager(this));
        treatRV.setAdapter(new TreatmentCardAdapter(treatmentData));
    }

    static class TreatmentCardHolder extends RecyclerView.ViewHolder {
        TextView dateCuringLabel;
        TextView curingDescLabel;
        public TreatmentCardHolder(@NonNull View itemView) {
            super(itemView);
            dateCuringLabel = itemView.findViewById(R.id.dateCuringLabel);
            curingDescLabel = itemView.findViewById(R.id.curingDescLabel);
        }
    }

    static class TreatmentCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final List<TreatmentData> treatmentDataList;
        public TreatmentCardAdapter(List<TreatmentData> treatmentDataList) { this.treatmentDataList = treatmentDataList; }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chronology_card, parent, false);
            return new TreatmentCardHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            TreatmentData treatmentData = treatmentDataList.get(position);
            TreatmentCardHolder treatmentCardHolder = (TreatmentCardHolder) holder;

            String dateString = treatmentData.getCreated_at();
            String formattedTime = "Запись от " + dateString;
            treatmentCardHolder.dateCuringLabel.setText(formattedTime);

            treatmentCardHolder.curingDescLabel.setText(treatmentData.getTreatment());
        }

        @Override
        public int getItemCount() {
            return treatmentDataList.size();
        }
    }
}