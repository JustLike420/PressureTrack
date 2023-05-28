package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import com.google.gson.annotations.SerializedName;

public class Measurment {
    @SerializedName("top")
    public int top;

    @SerializedName("bottom")
    public int bottom;

    @SerializedName("pulse")
    public int pulse;

    @SerializedName("comment")
    public String comment;

    public Measurment(int top, int bottom, int pulse, String comment) {
        this.top = top;
        this.bottom = bottom;
        this.pulse = pulse;
        this.comment = comment;
    }
}
