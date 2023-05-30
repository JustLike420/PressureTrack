package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients.databinding.ActivityAddRecordBinding;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class AddRecordActivity extends AppCompatActivity {

    private ActivityAddRecordBinding binding;
    APIInterface apiInterface;
    private String token;
    private Button newRecButton;
    private EditText pressET, commentET;
    private View backView;
    private ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        pressET = binding.pressET;
        commentET = binding.commentET;
        newRecButton = binding.newRecButton;

        newRecButton.setOnClickListener(view -> {
            if (isInputValid(pressET.getText().toString().trim())) {
                apiInterface = APIClient.getClient().create(APIInterface.class);
                String[] numbers = pressET.getText().toString().trim().split("\\s+");
                Measurment measurment = new Measurment(Integer.parseInt(numbers[0]),
                        Integer.parseInt(numbers[1]), Integer.parseInt(numbers[2]),
                        commentET.getText().toString().trim());
                Call<Void> call = apiInterface.sendRecord("Token " + token, measurment);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        System.out.println(response.body());
                        if (response.isSuccessful()) {
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        call.cancel();
                    }
                });
            }
            finish();
        });

        // Кнопка назад
        backView = binding.backViewAR;
        backIcon = binding.backIconAR;
        backIcon.setOnClickListener(view -> { finish(); });
        backView.setOnClickListener(view -> { finish(); });
    }

    private boolean isInputValid(String rec) {
        String[] numbers = rec.split("\\s+");

        if (numbers.length != 3) {
            return false;
        }

        for (String number : numbers) {
            if (!number.matches("\\d+")) {
                return false;
            }
        }

        return true;
    }
}