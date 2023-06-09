package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import com.google.gson.annotations.SerializedName;

public class LoginData {

    @SerializedName("email")
    public String email;
    @SerializedName("password")
    public String password;

    public LoginData(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
