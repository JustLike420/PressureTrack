package ru.mirea.zhalyaletdinov_f_n.doctorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import ru.mirea.zhalyaletdinov_f_n.doctorapp.databinding.ActivityArchivedPatientBinding;

public class ArchivedPatientActivity extends AppCompatActivity {
    ActivityArchivedPatientBinding binding;
    APIInterface apiInterface;
    private String token, pk;
    private PatientInfo patientInfo;
    private Button archiveButton, treatmentChronoButton;
    private ImageView backImage;
    private View backView;
    private RecyclerView rv1, rv2;
    LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArchivedPatientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("doctor_token", "");

        Intent intent = getIntent();
        patientInfo = (PatientInfo) intent.getSerializableExtra("PatientInfo");
        pk = intent.getStringExtra("PK");

        mainLoader(patientInfo);
        measLoader(token, pk);

        archiveButton = binding.archiveButton;
        archiveButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialog);
            builder.setTitle("Перемещение из архива");
            builder.setMessage("Вы уверены, что хотите разархивировать этого пациента?");
            builder.setPositiveButton("Разархивировать", (dialog, which) -> performArchivePatient(token, pk));
            builder.setNegativeButton("Отмена", (dialog, which) -> {});
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        treatmentChronoButton = binding.treatmentChronoButton;
        treatmentChronoButton.setOnClickListener(view -> {
            Intent chronoIntent = new Intent(this, ChronologyActivity.class);
            chronoIntent.putExtra("PK", pk);
            startActivity(chronoIntent);
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

    private void mainLoader(PatientInfo patientInfo) {
        String fio = patientInfo.getUser().getLastName() + " " + patientInfo.getUser().getFirstName();
        binding.patientNameLabel.setText(fio);
        if (patientInfo.getUser().getPhone() == null)
            binding.patientPhoneLabel.setText("Нет номера телефона");
        else
            binding.patientPhoneLabel.setText(patientInfo.getUser().getPhone());

        String dateString = patientInfo.getTreatment_start();
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDateTime dateTime = LocalDateTime.parse(dateString, inputFormatter);
        String formattedTime = "Начало лечения: " + dateTime.format(outputFormatter);
        binding.cureDateLabel.setText(formattedTime);

        String params = "Рост: " + patientInfo.getHeight() + "см\n" +
                "Вес: " + patientInfo.getWeight() + "кг\n" +
                "Тонометр: " + patientInfo.getDevice();
        binding.parametresLabel.setText(params);
    }

    private void measLoader(String token, String pk) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<GetMeasurment>> call = apiInterface.getMeasList("Token " + token, pk);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ArchivedPatientActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка");
                        builder.setMessage("Не удалось получить данные о измерениях пациента!");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else if (response.code() == 401) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ArchivedPatientActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка аутентификации");
                        builder.setMessage("Неправильный токен аутентификации");
                        builder.setPositiveButton("ОК", (dialog, which) -> {
                            clearToken();
                            Intent intent = new Intent(ArchivedPatientActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else if (response.code() == 500) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ArchivedPatientActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Внутренняя ошибка сервера");
                        builder.setMessage("Произошла внутренняя ошибка сервера. Попробуйте позже.");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ArchivedPatientActivity.this, R.style.MyAlertDialog);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(ArchivedPatientActivity.this, R.style.MyAlertDialog);
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

    private void performArchivePatient(String token, String pk) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<Void> call = apiInterface.archivePatient("Token " + token, pk);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> finish());
                } else if (response.code() == 400) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ArchivedPatientActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка");
                        builder.setMessage("Не получить данные пациента. Попробуйте позже.");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else if (response.code() == 401) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ArchivedPatientActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка аутентификации");
                        builder.setMessage("Неправильный токен аутентификации");
                        builder.setPositiveButton("ОК", (dialog, which) -> {
                            clearToken();
                            Intent intent = new Intent(ArchivedPatientActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else if (response.code() == 500) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ArchivedPatientActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Внутренняя ошибка сервера");
                        builder.setMessage("Произошла внутренняя ошибка сервера. Попробуйте позже.");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ArchivedPatientActivity.this, R.style.MyAlertDialog);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(ArchivedPatientActivity.this, R.style.MyAlertDialog);
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
        if (measList.size() > 7)
            weekMeasList = measList.subList(0, Math.min(measList.size(), 7));
        else
            weekMeasList = measList;
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        String beginDate, endDate;

        if (weekMeasList.size() > 1) {
            beginDate = weekMeasList.get(weekMeasList.size()-1).getCreated_at();
            endDate = weekMeasList.get(0).getCreated_at();
        }
        else {
            beginDate = weekMeasList.get(0).getCreated_at();
            endDate = weekMeasList.get(0).getCreated_at();
        }
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