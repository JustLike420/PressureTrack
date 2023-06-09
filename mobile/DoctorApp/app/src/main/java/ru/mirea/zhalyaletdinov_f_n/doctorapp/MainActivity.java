package ru.mirea.zhalyaletdinov_f_n.doctorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mirea.zhalyaletdinov_f_n.doctorapp.databinding.ActivityMainBinding;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    APIInterface apiInterface;
    private String token;
    private ActivityMainBinding binding;
    private Button logoutButton;
    private ImageButton searchButton;
    RecyclerView recyclerView;
    private TabLayout tabLayout;
    private List<PatientCard> activePatientList, archivedPatientList;
    private List<PatientCard> activeQueryList, archivedQueryList;
    private EditText searchEditText;
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("doctor_token", "");
        System.out.println(token);

        mainloader(token);
        activePatientsLoader(token);
        archivedPatientsLoader(token);

        logoutButton = binding.logoutButton;
        logoutButton.setOnClickListener(view -> performLogout(token));

        searchButton = binding.searchButton;
        searchButton.setOnClickListener(view -> {
            if (binding.searchEditText.getVisibility() == View.GONE)
                binding.searchEditText.setVisibility(View.VISIBLE);
            else
                binding.searchEditText.setVisibility(View.GONE);
        });

        tabLayout = binding.tabLayout;
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    initializePatientsCards(archivedPatientList);
                    logoutButton.setVisibility(View.INVISIBLE);
                    logoutButton.setActivated(false);
                }
                else {
                    initializePatientsCards(activePatientList);
                    logoutButton.setVisibility(View.VISIBLE);
                    logoutButton.setActivated(true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        binding.searchEditText.setVisibility(View.GONE);
        mainloader(token);
        activePatientsLoader(token);
        archivedPatientsLoader(token);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainloader(token);
        activePatientsLoader(token);
        archivedPatientsLoader(token);
    }

    private void clearToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("doctor_token", "");
        editor.apply();
    }

    private void mainloader(String token) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<AccountProfile> call = apiInterface.mainLoader("Token " + token);
        call.enqueue(new Callback<AccountProfile>() {
            @Override
            public void onResponse(Call<AccountProfile> call, Response<AccountProfile> response) {
                if (response.isSuccessful()) {
                    AccountProfile accountProfile = response.body();
                    assert accountProfile != null;
                    String name = accountProfile.getName();
                    String last_name = accountProfile.getLastName();
                    String text = "Добрый день,\n" + last_name + " " + name;
                    runOnUiThread(() -> binding.docnameTV.setText(text));
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
                            clearToken();
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
            public void onFailure(@NonNull Call<AccountProfile> call, @NonNull Throwable t) {
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

    private void activePatientsLoader(String token) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<PatientCard>> call = apiInterface.getPatientList("Token " + token, "True");
        call.enqueue(new Callback<List<PatientCard>>() {
            @Override
            public void onResponse(@NonNull Call<List<PatientCard>> call, @NonNull Response<List<PatientCard>> response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        activeQueryList = response.body();
                        activePatientList = response.body();
                        initializePatientsCards(activePatientList);
                    });
                } else if (response.code() == 400) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка");
                        builder.setMessage("Не удалось загрузить список пациентов. Попробуйте позже.");
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
                            clearToken();
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
            public void onFailure(@NonNull Call<List<PatientCard>> call, @NonNull Throwable t) {
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

    private void archivedPatientsLoader(String token) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<PatientCard>> call = apiInterface.getPatientList("Token " + token, "False");
        call.enqueue(new Callback<List<PatientCard>>() {
            @Override
            public void onResponse(@NonNull Call<List<PatientCard>> call, @NonNull Response<List<PatientCard>> response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        archivedQueryList = response.body();
                        archivedPatientList = response.body();
                    });
                } else if (response.code() == 400) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка");
                        builder.setMessage("Не удалось загрузить список пациентов. Попробуйте позже.");
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
                            clearToken();
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
            public void onFailure(@NonNull Call<List<PatientCard>> call, @NonNull Throwable t) {
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

    private void performLogout(String token) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<Void> call = apiInterface.logout("Token " + token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("doctor_token", "");
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
                            clearToken();
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

    private void initializePatientsCards(List<PatientCard> patientCardList) {
        recyclerView = binding.rvMain;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        PatientCardAdapter patientCardAdapter = new PatientCardAdapter(patientCardList);
        patientCardAdapter.setOnItemClickListener(data -> {
            String pk = data.pk;
            stepIntoPatientProfile(token, pk);
        });
        searchEditText = binding.searchEditText;
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String searchQuery = s.toString();
                patientCardAdapter.filter(searchQuery);
            }
        });
        recyclerView.setAdapter(patientCardAdapter);
    }

    private void stepIntoPatientProfile(String token, String pk) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<PatientInfo> call = apiInterface.getPatientInfo("Token " + token, pk);
        call.enqueue(new Callback<PatientInfo>() {
            @Override
            public void onResponse(@NonNull Call<PatientInfo> call, @NonNull Response<PatientInfo> response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        PatientInfo patientInfo = response.body();
                        Intent intent;
                        assert patientInfo != null;
                        if (patientInfo.getStatus().equals("true")) {
                            intent = new Intent(MainActivity.this, ActivePatientActivity.class);
                            intent.putExtra("PatientInfo", patientInfo);
                            intent.putExtra("PK", pk);
                            startActivity(intent);
                        } else if (patientInfo.getStatus().equals("false")) {
                            intent = new Intent(MainActivity.this, ArchivedPatientActivity.class);
                            intent.putExtra("PatientInfo", patientInfo);
                            intent.putExtra("PK", pk);
                            startActivity(intent);
                        }
                    });
                } else if (response.code() == 400) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка");
                        builder.setMessage("Не получить данные пациента. Попробуйте позже.");
                        builder.setPositiveButton("ОК", (dialog, which) -> {});
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                } else if (response.code() == 401) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("Ошибка аутентификации");
                        builder.setMessage("Неправильный токен аутентификации");
                        builder.setPositiveButton("ОК", (dialog, which) -> {
                            clearToken();
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
            public void onFailure(@NonNull Call<PatientInfo> call, @NonNull Throwable t) {
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

    static class PatientCardHolder extends RecyclerView.ViewHolder {
        TextView patientName;
        TextView patientPhone;
        TextView cureDate;
        private String pk;
        public PatientCardHolder(@NonNull View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.patientName);
            patientPhone = itemView.findViewById(R.id.patientPhone);
            cureDate = itemView.findViewById(R.id.cureDate);
        }
        public void setPK(String pk) { this.pk = pk; }
    }

    static class PatientCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final List<PatientCard> patientCardList;
        private List<PatientCard> filteredList;

        public PatientCardAdapter(List<PatientCard> patientCardList) {
            this.filteredList = new ArrayList<>(patientCardList);
            this.patientCardList = patientCardList;
        }
        private OnItemClickListener listener;

        public interface OnItemClickListener { void onItemClick(PatientCardHolder data); }
        public void setOnItemClickListener(OnItemClickListener listener) { this.listener = listener; }

        public void filter(String query) {
            filteredList.clear();
            if (query.isEmpty()) {
                filteredList.addAll(patientCardList);
            } else {
                query = query.toLowerCase();
                for (PatientCard patientCard : patientCardList) {
                    String name = patientCard.getUser().getLast_name() + " " + patientCard.getUser().getFirst_name();
                    String phone = patientCard.getUser().getPhone() != null ? patientCard.getUser().getPhone() : "";
                    if (name.toLowerCase().contains(query) || phone.toLowerCase().contains(query)) {
                        filteredList.add(patientCard);
                    }
                }
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_card, parent, false);
            return new PatientCardHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            PatientCard patientCard = filteredList.get(position);
            PatientCardHolder patientCardHolder = (PatientCardHolder) holder;
            patientCardHolder.setPK(patientCard.getPK());

            String fio = patientCard.getUser().getLast_name() + " " + patientCard.getUser().getFirst_name();
            patientCardHolder.patientName.setText(fio);

            if (patientCard.getUser().getPhone() == null)
                patientCardHolder.patientPhone.setText("Нет номера телефона");
            else
                patientCardHolder.patientPhone.setText(patientCard.getUser().getPhone());

            String dateString = patientCard.getTreatment_start();
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDateTime dateTime = LocalDateTime.parse(dateString, inputFormatter);
            String formattedTime = "Начало лечения: " + dateTime.format(outputFormatter);
            patientCardHolder.cureDate.setText(formattedTime);

            patientCardHolder.itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onItemClick(patientCardHolder);
                }
            });
        }

        @Override
        public int getItemCount() {
            return filteredList.size();
        }
    }
}