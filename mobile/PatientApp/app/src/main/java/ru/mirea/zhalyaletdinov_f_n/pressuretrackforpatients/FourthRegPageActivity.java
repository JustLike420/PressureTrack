package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import android.content.DialogInterface;
import ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients.databinding.FourthRegPageBinding;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class FourthRegPageActivity extends AppCompatActivity {

    private FourthRegPageBinding binding;
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
        Float height = getIntent().getFloatExtra("Height", 176);
        Float weight = getIntent().getFloatExtra("Weight", 60);
        String model = getIntent().getStringExtra("Model");

        regEndButton = binding.regEndButton;
        snilsInput = binding.snilsInput;

        regEndButton.setOnClickListener(view -> {
//                if (isValidSNILS(snilsInput)) {
//                    builder.setTitle("Введен неккоректный снилс");
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//                } else { }
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
                // API - функционал
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

//    public static boolean isValidSNILS(String snils) {
//        if (snils == null || snils.length() != 11)
//            return false;
//
//        String digitsOnly = snils.replaceAll("[^0-9]", "");
//        if (digitsOnly.length() != 9)
//            return false;
//
//        int sum = 0;
//        for (int i = 0; i < 9; i++)
//            sum += Integer.parseInt(digitsOnly.substring(i, i + 1)) * (9 - i);
//
//        int checkDigit = 0;
//        if (sum < 100)
//            checkDigit = sum;
//        else if (sum > 101) {
//            checkDigit = sum % 101;
//            if (checkDigit == 100)
//                checkDigit = 0;
//        }
//        return checkDigit == Integer.parseInt(digitsOnly.substring(9));
//    }
}
