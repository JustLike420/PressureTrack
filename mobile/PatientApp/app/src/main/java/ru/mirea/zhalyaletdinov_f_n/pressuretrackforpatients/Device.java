package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import com.google.gson.annotations.SerializedName;

public class Device {
    @SerializedName("device")
    public String device;

    public Device(String device) {
        this.device = device;
    }
}
