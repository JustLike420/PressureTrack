package ru.mirea.zhalyaletdinov_f_n.doctorapp;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("auth_token")
    public String token;

    @SerializedName("user_role")
    public String user_role;

    public String getAuthToken() {
        return token;
    }
    public String getUserRole() { return user_role; }
}

