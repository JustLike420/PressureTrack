package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients.databinding.ActivityMainBinding;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    APIInterface apiInterface;
    private String token;
    private BarChart chart;
    private Button addRecordButton;
    private Button logoutButton;
    private TextView newTonLabel;
    private RecyclerView rv1, rv2;
    private PatientProfile patientProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        System.out.println(token);
        mainLoader(token);
        treatmentLoad(token);

        rv1 = binding.rvToday;
        rv1.setLayoutManager(new LinearLayoutManager(this));
        rv1.setAdapter(new TodayCardsAdapter());

        rv2 = binding.rvWeek;
        rv2.setLayoutManager(new LinearLayoutManager(this));
        rv2.setAdapter(new WeekCardsAdapter());

        logoutButton = binding.logoutButton;
        logoutButton.setOnClickListener(view -> {
            apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<Void> call = apiInterface.logout("Token " + token);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        saveTokenToSharedPreferences("");

                        runOnUiThread(() -> {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
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
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    call.cancel();
                }
            });
        });

        addRecordButton = binding.addRecordButton;
        addRecordButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddRecordActivity.class);
            startActivity(intent);
        });

        newTonLabel = binding.newTonLabel;
        newTonLabel.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NewTonometrActivity.class);
            startActivity(intent);
        });
    }

    private void mainLoader(String token) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<PatientProfile> call = apiInterface.mainLoader("Token " + token);
        call.enqueue(new Callback<PatientProfile>() {
            @Override
            public void onResponse(@NonNull Call<PatientProfile> call, @NonNull Response<PatientProfile> response) {
                if (response.isSuccessful()){
                    patientProfile = response.body();
                    String name = patientProfile.getUser().getFirstName();
                    String last_name = patientProfile.getUser().getLastName();
                    String device = patientProfile.getDevice();

                    runOnUiThread(() -> {
                        String text = "Добрый день,\n" + name + " " + last_name;
                        binding.welcomeLabel.setText(text);
                        binding.deviceModelLabel.setText(device);
                    });


                } else {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
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
            public void onFailure(@NonNull Call<PatientProfile> call, @NonNull Throwable t) {
                call.cancel();
            }
        });
    }

    private void treatmentLoad(String token) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Treatment>> call = apiInterface.getTreatment("Token " + token);
        call.enqueue(new Callback<List<Treatment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Treatment>> call, @NonNull Response<List<Treatment>> response) {
                if (response.isSuccessful()) {
                    List<Treatment> list_treatment = response.body();
                    Treatment treatment = list_treatment.get(0);
                    String message = treatment.getMessage();
                    runOnUiThread(() -> {
                        binding.treatmentText.setText(message);
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Treatment>> call, @NonNull Throwable t) {
                call.cancel();
            }
        });
    }

    private void saveTokenToSharedPreferences(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    class RecordCards extends RecyclerView.ViewHolder {
        public RecordCards(@NonNull View itemView) {
            super(itemView);
        }
    }

    class TodayCardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_row, parent, false);
            return new RecordCards(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) { }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    class WeekCardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_row, parent, false);
            return new RecordCards(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) { }

        @Override
        public int getItemCount() {
            return 7;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mainLoader(token);
    }
}