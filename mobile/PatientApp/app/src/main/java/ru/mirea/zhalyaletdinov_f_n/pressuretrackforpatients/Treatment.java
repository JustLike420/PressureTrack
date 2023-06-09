package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import com.google.gson.annotations.SerializedName;

public class Treatment {
    @SerializedName("message")
    public String message;

    @SerializedName("created_at")
    public String created_at;

    public Treatment(String message, String created_at) {
        this.message = message;
        this.created_at = created_at;
    }

    public String getMessage() {
        return this.message;
    }
}
