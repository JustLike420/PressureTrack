package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    APIInterface apiInterface;
    private String token;
    private LineChart lineChart;
    private Button addRecordButton;
    private Button logoutButton;
    private TextView newTonLabel;
    private RecyclerView rv1, rv2;
    private PatientProfile patientProfile;
    List<GetMeasurment> measList;
    List<GetMeasurment> weekMeasList;

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
        getMeasurementList(token);
        System.out.println(measList);

        rv1 = binding.rvToday;
        rv1.setLayoutManager(new LinearLayoutManager(this));
        rv1.setAdapter(new TodayCardsAdapter());

//        rv2 = binding.rvWeek;
//        rv2.setLayoutManager(new LinearLayoutManager(this));
//        rv2.setAdapter(new WeekCardsAdapter());

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

    private void initializeWeekRecords(List<GetMeasurment> measurements) {
        rv2 = binding.rvWeek;
        rv2.setLayoutManager(new LinearLayoutManager(this));
        rv2.setAdapter(new WeekCardsAdapter(measurements));
    }

    private void getMeasurementList(String token) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<GetMeasurment>> call = apiInterface.getMeasList("Token " + token);
        call.enqueue(new Callback<List<GetMeasurment>>() {
            @Override
            public void onResponse(@NonNull Call<List<GetMeasurment>> call, @NonNull Response<List<GetMeasurment>> response) {
                if (response.isSuccessful()) {
                    measList = response.body();
                    weekMeasList = findHighestTopPerDay(measList);
                    Collections.reverse(weekMeasList);
                    runOnUiThread(() -> {
                        initializeWeekRecords(weekMeasList);
                    });
                } else {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка");
                        builder.setMessage("Не удалось получить данные о ваших измерениях!");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        Log.e("Response", response.toString());
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GetMeasurment>> call, @NonNull Throwable t) {
                call.cancel();
            }
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
        TextView todayPres;
        TextView todayHB;
        TextView todayDate;

        public RecordCards(@NonNull View itemView) {
            super(itemView);
            todayPres = itemView.findViewById(R.id.todayPres);
            todayHB = itemView.findViewById(R.id.todayHB);
            todayDate = itemView.findViewById(R.id.todayDate);
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
        private List<GetMeasurment> meas;
        public WeekCardsAdapter(List<GetMeasurment> meas) { this.meas = meas; }

        @SuppressLint("NotifyDataSetChanged")
        public void setData(List<GetMeasurment> meas) {
            this.meas = meas;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_row, parent, false);
            return new RecordCards(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            GetMeasurment measurement = meas.get(position);
            RecordCards recordCard = (RecordCards) holder;

            String todayPres = measurement.getTop() + "/" + measurement.getBottom();
            recordCard.todayPres.setText(todayPres);
            recordCard.todayHB.setText(String.valueOf(measurement.getPulse()));
            String dateString = measurement.getCreated_at();
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            LocalDateTime dateTime = LocalDateTime.parse(dateString, inputFormatter);
            String formattedDate = dateTime.format(outputFormatter);
            recordCard.todayDate.setText(formattedDate);
        }

        @Override
        public int getItemCount() {
            return Math.min(meas.size(), 7);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mainLoader(token);
    }

    private List<GetMeasurment> findHighestTopPerDay(List<GetMeasurment> measurements) {
        Map<String, GetMeasurment> highestTops = new HashMap<>();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");

        for (GetMeasurment measurement : measurements) {
            String date = measurement.getCreated_at();

            try {
                Date measurementDate = inputFormat.parse(date);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(measurementDate);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                String formattedDate = inputFormat.format(calendar.getTime());

                if (highestTops.containsKey(formattedDate)) {
                    GetMeasurment existingMeasurement = highestTops.get(formattedDate);
                    int currentTop = measurement.getTop();
                    int highestTop = existingMeasurement.getTop();

                    if (currentTop > highestTop) {
                        highestTops.put(formattedDate, measurement);
                    }
                } else {
                    highestTops.put(formattedDate, measurement);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        List<GetMeasurment> highestTopsList = new ArrayList<>(highestTops.values());
        Collections.sort(highestTopsList, new Comparator<GetMeasurment>() {
            @Override
            public int compare(GetMeasurment measurement1, GetMeasurment measurement2) {
                try {
                    Date date1 = inputFormat.parse(measurement1.getCreated_at());
                    Date date2 = inputFormat.parse(measurement2.getCreated_at());
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        return highestTopsList;
    }

}