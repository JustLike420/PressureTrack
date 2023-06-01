package ru.mirea.zhalyaletdinov_f_n.doctorapp;

import com.google.gson.annotations.SerializedName;

public class TreatmentData {
    @SerializedName("message")
    private String treatment;

    public TreatmentData(String treatment) { this.treatment = treatment; }
}
