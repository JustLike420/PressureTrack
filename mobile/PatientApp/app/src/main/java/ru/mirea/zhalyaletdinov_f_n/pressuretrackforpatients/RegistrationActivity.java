package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private Button regWButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this, R.style.MyAlertDialog);
        builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });

        EditText fioTV = findViewById(R.id.fioInput);
        EditText emailTV = findViewById(R.id.emailInput);
        EditText passwordTV = findViewById(R.id.passwordRegInput);

        regWButton = findViewById(R.id.regWButton);
        regWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fioTV.getText().toString().isEmpty() || emailTV.getText().toString().isEmpty() ||
                        passwordTV.getText().toString().isEmpty()) {
                    builder.setTitle("Заполните все поля");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    String[] fio = fioTV.getText().toString().split(" ");

                    if (!isValidFIO(fioTV.getText().toString())) {
                        builder.setTitle("Вы неправильно заполнили имя и фамилию");
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else if (!isValidEmail(emailTV.getText().toString()) && !isValidPhoneNumber(emailTV.getText().toString())) {
                        builder.setTitle("Неверный номер телефона или email");
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else {
                        Intent intentReg = new Intent(RegistrationActivity.this, SecRegPageActivity.class);
//                        ArrayList<String> firstPage = new ArrayList<String>();
//                        firstPage.add(fio[0]);
//                        firstPage.add(fio[1]);
//                        firstPage.add(emailTV.getText().toString());
//                        firstPage.add(passwordTV.getText().toString());
                        intentReg.putExtra("Name", fio[0]);
                        intentReg.putExtra("Surname", fio[1]);
                        intentReg.putExtra("Email", emailTV.getText().toString());
                        intentReg.putExtra("Password", passwordTV.getText().toString());
                        startActivity(intentReg);
                    }
                }
            }
        });
    }

    public void AuthPageOnButtonClick(View view) {
        finish();
    }

    public boolean isValidFIO(String input) {
        String regex = "^[А-Яа-я]+ [А-Яа-я]+$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        return matcher.matches();
    }

    public static boolean isValidPhoneNumber(String phone) {
        String phoneRegex = "^[0-9]{10,13}$";
        Pattern pattern = Pattern.compile(phoneRegex);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

//    public boolean isValidPassword(String password) {
//        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(password);
//        return matcher.matches();
//    }
}