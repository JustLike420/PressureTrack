package ru.mirea.zhalyaletdinov_f_n.doctorapp;

import com.google.gson.annotations.SerializedName;

public class TreatmentData {
    @SerializedName("message")
    private String treatment;

    @SerializedName("created_at")
    private String created_at;

    public TreatmentData(String treatment) { this.treatment = treatment; }

    public TreatmentData(String treatment, String created_at) {
        this.treatment = treatment;
        this.created_at = created_at;
    }

    public String getTreatment() { return this.treatment; }
    public String getCreated_at() { return this.created_at; }
}
