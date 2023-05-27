package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("auth_token")
    public String token;

    public String getAuthToken() {
        return token;
    }
}
