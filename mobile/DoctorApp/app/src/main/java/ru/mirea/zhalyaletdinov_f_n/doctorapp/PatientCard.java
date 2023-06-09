package ru.mirea.zhalyaletdinov_f_n.doctorapp;

import com.google.gson.annotations.SerializedName;

public class PatientCard {
    @SerializedName("pk")
    private String pk;
    @SerializedName("treatment_start")
    private String treatment_start;
    @SerializedName("user")
    private User user;
    public String getTreatment_start() { return this.treatment_start; }
    public User getUser() { return this.user; }
    public String getPK() { return this.pk; }

    public static class User {
        @SerializedName("first_name")
        private String first_name;
        @SerializedName("last_name")
        private String last_name;
        @SerializedName("phone")
        private String phone;

        public String getFirst_name() { return this.first_name; }
        public String getLast_name() { return last_name; }
        public String getPhone() { return phone; }
    }
}
