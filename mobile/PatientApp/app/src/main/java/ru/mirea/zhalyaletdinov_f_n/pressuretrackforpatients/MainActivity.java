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

import com.github.mikephil.charting.charts.BarChart;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Button logoutButton;
    APIInterface apiInterface;
    private BarChart chart;
    private ActivityMainBinding binding;
    private Button addRecordButton;
    private RecyclerView rv1, rv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        System.out.println(token);
        mainLoader(token);

        rv1 = binding.rvToday;
        rv1.setLayoutManager(new LinearLayoutManager(this));
        rv1.setAdapter(new TodayCardsAdapter());

        rv2 = binding.rvWeek;
        rv2.setLayoutManager(new LinearLayoutManager(this));
        rv2.setAdapter(new WeekCardsAdapter());

        logoutButton = binding.logoutButton;
        logoutButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        addRecordButton = binding.addRecordButton;
        addRecordButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddRecordActivity.class);
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
                    PatientProfile patientProfile = response.body();
                    String name = patientProfile.getUser().getFirstName();
                    String last_name = patientProfile.getUser().getLastName();
                    String device = patientProfile.getDevice();

                    runOnUiThread(() -> {
                        String text = binding.welcomeLabel.getText().toString() + name + " " + last_name;
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

    public void newModOnClick(View view) {
        Intent intent = new Intent(MainActivity.this, NewTonometrActivity.class);
        startActivity(intent);
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

}