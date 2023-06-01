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
import java.time.LocalDate;
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

        logoutButton = binding.logoutButton;
        logoutButton.setOnClickListener(view -> logoutProcess(token));

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

    @Override
    protected void onRestart() {
        super.onRestart();
        mainLoader(token);
        treatmentLoad(token);
        getMeasurementList(token);
    }

    /* -----------------------------------------------------------------------------
       -------------------- Получение данных о профиле пациента --------------------
       ----------------------------------------------------------------------------- */

    private void mainLoader(String token) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<PatientProfile> call = apiInterface.mainLoader("Token " + token);
        call.enqueue(new Callback<PatientProfile>() {
            @Override
            public void onResponse(@NonNull Call<PatientProfile> call, @NonNull Response<PatientProfile> response) {
                if (response.isSuccessful()){
                    patientProfile = response.body();
                    assert patientProfile != null;
                    String name = patientProfile.getUser().getFirstName();
                    String last_name = patientProfile.getUser().getLastName();
                    String device = patientProfile.getDevice();

                    runOnUiThread(() -> {
                        String text = "Добрый день,\n" + last_name + " " + name;
                        binding.welcomeLabel.setText(text);
                        binding.deviceModelLabel.setText(device);
                    });
                } else if (response.code() == 400) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка");
                        builder.setMessage("Не удалось получить данные о Вашем профиле");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        Log.e("Response", response.toString());
                    });
                } else if (response.code() == 401) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка аутентификации");
                        builder.setMessage("Неправильный токен аутентификации");
                        builder.setPositiveButton("ОК", (dialog, which) -> {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else if (response.code() == 500) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Внутренняя ошибка сервера");
                        builder.setMessage("Произошла внутренняя ошибка сервера. Попробуй позже.");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка сервера");
                        builder.setMessage("Произошла ошибка при обращении к серверу.");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<PatientProfile> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
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

    /* -----------------------------------------------------------------------------
       ------------------ Получение данных о назначенном лечении -------------------
       ----------------------------------------------------------------------------- */

    private void treatmentLoad(String token) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Treatment>> call = apiInterface.getTreatment("Token " + token);
        call.enqueue(new Callback<List<Treatment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Treatment>> call, @NonNull Response<List<Treatment>> response) {
                if (response.isSuccessful()) {
                    List<Treatment> list_treatment = response.body();
                    assert list_treatment != null;
                    Treatment treatment = list_treatment.get(0);
                    String message = treatment.getMessage();
                    runOnUiThread(() -> binding.treatmentText.setText(message));
                } else if (response.code() == 400) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка");
                        builder.setMessage("Не удалось получить данные о назначенном Вам лечении");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        Log.e("Response", response.toString());
                    });
                } else if (response.code() == 401) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка аутентификации");
                        builder.setMessage("Неправильный токен аутентификации");
                        builder.setPositiveButton("ОК", (dialog, which) -> {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else if (response.code() == 500) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Внутренняя ошибка сервера");
                        builder.setMessage("Произошла внутренняя ошибка сервера. Попробуй позже.");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка сервера");
                        builder.setMessage("Произошла ошибка при обращении к серверу.");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Treatment>> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
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

    /* -----------------------------------------------------------------------------
       ------------------ Получение данных о сделанных измерениях ------------------
       ----------------------------------------------------------------------------- */

    private void getMeasurementList(String token) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<GetMeasurment>> call = apiInterface.getMeasList("Token " + token);
        call.enqueue(new Callback<List<GetMeasurment>>() {
            @Override
            public void onResponse(@NonNull Call<List<GetMeasurment>> call, @NonNull Response<List<GetMeasurment>> response) {
                if (response.isSuccessful()) {
                    List<GetMeasurment> measList, weekMeasList, todayMeasList;
                    measList = response.body();
                    assert measList != null;
                    weekMeasList = findHighestTopPerDay(measList);
                    Collections.reverse(weekMeasList);
                    todayMeasList = getMeasurementsWithTodayDate(measList);
                    Collections.reverse(todayMeasList);

                    runOnUiThread(() -> {
                        initializeRecyclerView(weekMeasList, todayMeasList);
                        if (weekMeasList.size() > 0)
                            initializeLineChart(weekMeasList);
                    });
                } else if (response.code() == 400) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка");
                        builder.setMessage("Не удалось получить данные о ваших измерениях!");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        Log.e("Response", response.toString());
                    });
                } else if (response.code() == 401) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка аутентификации");
                        builder.setMessage("Неправильный токен аутентификации");
                        builder.setPositiveButton("ОК", (dialog, which) -> {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else if (response.code() == 500) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Внутренняя ошибка сервера");
                        builder.setMessage("Произошла внутренняя ошибка сервера. Попробуй позже.");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка сервера");
                        builder.setMessage("Произошла ошибка при обращении к серверу.");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<GetMeasurment>> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
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

    private void logoutProcess(String token) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<Void> call = apiInterface.logout("Token " + token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", "");
                    editor.apply();

                    runOnUiThread(() -> {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else if (response.code() == 400) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка");
                        builder.setMessage("Не удалось покинуть личный кабинет. Попробуйте позже.");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        Log.e("Response", response.toString());
                    });
                } else if (response.code() == 401) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка аутентификации");
                        builder.setMessage("Неправильный токен аутентификации");
                        builder.setPositiveButton("ОК", (dialog, which) -> {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else if (response.code() == 500) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Внутренняя ошибка сервера");
                        builder.setMessage("Произошла внутренняя ошибка сервера. Попробуй позже.");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
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

    /* -----------------------------------------------------------------------------
       ---------------------- Обработка полученных измерений -----------------------
       ----------------------------------------------------------------------------- */

    public static List<GetMeasurment> sortByDate(List<GetMeasurment> measList) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        Comparator<GetMeasurment> comparator = Comparator.comparing(meas -> LocalDateTime.parse(meas.getCreated_at(), formatter));
        measList.sort(comparator);
        Collections.reverse(measList);
        return measList;
    }

    private List<GetMeasurment> findHighestTopPerDay(List<GetMeasurment> measurements) {
        // Выбор самого высокого давления за день, если в день было несколько записей
        Map<String, GetMeasurment> highestTops = new HashMap<>();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");

        for (GetMeasurment measurement : measurements) {
            String date = measurement.getCreated_at();

            try {
                Date measurementDate = inputFormat.parse(date);

                Calendar calendar = Calendar.getInstance();
                assert measurementDate != null;
                calendar.setTime(measurementDate);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                String formattedDate = inputFormat.format(calendar.getTime());

                if (highestTops.containsKey(formattedDate)) {
                    GetMeasurment existingMeasurement = highestTops.get(formattedDate);
                    int currentTop = measurement.getTop();
                    assert existingMeasurement != null;
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

        // Сортировка записей за последние 7 дней по дате
        List<GetMeasurment> highestTopsList = new ArrayList<>(highestTops.values());
        highestTopsList.sort((measurement1, measurement2) -> {
            try {
                Date date1 = inputFormat.parse(measurement1.getCreated_at());
                Date date2 = inputFormat.parse(measurement2.getCreated_at());
                assert date1 != null;
                return date1.compareTo(date2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });

        return highestTopsList;
    }

    private static List<GetMeasurment> getMeasurementsWithTodayDate(List<GetMeasurment> measurements) {
        // Выбор данных с сегодняшней датой
        List<GetMeasurment> todayMeasurements = new ArrayList<>();

        LocalDate currentDate = LocalDate.now();

        for (GetMeasurment measurement : measurements) {
            LocalDateTime dateTime = LocalDateTime.parse(measurement.getCreated_at(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            LocalDate measurementDate = dateTime.toLocalDate();

            if (measurementDate.equals(currentDate)) {
                todayMeasurements.add(measurement);
            }
        }
        return todayMeasurements;
    }

    /* -----------------------------------------------------------------------------
       ---------------------------- Построение графика -----------------------------
       ----------------------------------------------------------------------------- */

    private void initializeLineChart(List<GetMeasurment> measList) {
        List<GetMeasurment> weekMeasList = null;
        if (weekMeasList.size() > 7)
            weekMeasList = measList.subList(0, Math.min(measList.size(), 7));
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        String beginDate = weekMeasList.get(weekMeasList.size()-1).getCreated_at();
        String endDate = weekMeasList.get(0).getCreated_at();
        binding.beginDateET.setText(LocalDateTime.parse(beginDate, inputFormatter).format(outputFormatter).trim());
        binding.endDateET.setText(LocalDateTime.parse(endDate, inputFormatter).format(outputFormatter).trim());

        lineChart = binding.LineChart;
        LineChartBuilder chartBuilder = new LineChartBuilder(getApplicationContext(), lineChart, weekMeasList);
        chartBuilder.buildChart();
    }

    /* -----------------------------------------------------------------------------
       -------------------------- Обновление RecyclerView --------------------------
       ----------------------------------------------------------------------------- */

    private void initializeRecyclerView(List<GetMeasurment> weekMeas, List<GetMeasurment> todayMeas) {
        rv1 = binding.rvToday;
        rv2 = binding.rvWeek;

        if (todayMeas.size() > 0) {
            rv1.setLayoutManager(new LinearLayoutManager(this));
            rv1.setAdapter(new TodayCardsAdapter(todayMeas));
        } else {
            rv1.setLayoutManager(new LinearLayoutManager(this));
            rv1.setAdapter(new NoDataCardAdapter("За сегодня не было сделано ни одного измерения."));
        }

        if (weekMeas.size() > 0) {
            rv2.setLayoutManager(new LinearLayoutManager(this));
            rv2.setAdapter(new WeekCardsAdapter(weekMeas));
        } else {
            rv2.setLayoutManager(new LinearLayoutManager(this));
            rv2.setAdapter(new NoDataCardAdapter("Нет измерений за последние 7 дней."));
        }
    }

    /* -----------------------------------------------------------------------------
       ---------------------- Заполнение RecyclerView данными ----------------------
       ----------------------------------------------------------------------------- */

    static class NoMeasDataCard extends RecyclerView.ViewHolder {
        TextView NoMeasLabel;
        public NoMeasDataCard(@NonNull View itemView) {
            super(itemView);
            NoMeasLabel = itemView.findViewById(R.id.NoMeasLabel);
        }
    }

    static class NoDataCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final String message;
        public NoDataCardAdapter(String message) { this.message = message; }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.no_measurments_row, parent, false);
            return new NoMeasDataCard(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            NoMeasDataCard noMeasDataCard = (NoMeasDataCard) holder;
            noMeasDataCard.NoMeasLabel.setText(message);
        }

        @Override
        public int getItemCount() { return 1; }
    }

    static class RecordCards extends RecyclerView.ViewHolder {
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

    static class TodayCardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final List<GetMeasurment> meas;
        public TodayCardsAdapter(List<GetMeasurment> meas) { this.meas = meas; }

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

            // Форматирование значения времени
            String dateString = measurement.getCreated_at();
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(dateString, inputFormatter);
            String formattedTime = dateTime.format(outputFormatter);
            recordCard.todayDate.setText(formattedTime);
        }

        @Override
        public int getItemCount() {
            return Math.min(meas.size(), 3);
        }
    }

    static class WeekCardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<GetMeasurment> meas;
        public WeekCardsAdapter(List<GetMeasurment> meas) {
            this.meas = meas.subList(0, Math.min(7, meas.size()));
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_row, parent, false);
            return new RecordCards(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            List<GetMeasurment> sortedMeas = sortByDate(meas);
            GetMeasurment measurement = sortedMeas.get(position);
            RecordCards recordCard = (RecordCards) holder;

            String todayPres = measurement.getTop() + "/" + measurement.getBottom();
            recordCard.todayPres.setText(todayPres);

            recordCard.todayHB.setText(String.valueOf(measurement.getPulse()));

            // Форматирование значения даты
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
}