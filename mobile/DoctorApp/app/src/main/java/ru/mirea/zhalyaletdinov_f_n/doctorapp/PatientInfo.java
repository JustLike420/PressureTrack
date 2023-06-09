package ru.mirea.zhalyaletdinov_f_n.doctorapp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PatientInfo implements Serializable {
    public static class User implements Serializable {
        @SerializedName("first_name")
        private String firstName;

        @SerializedName("last_name")
        private String lastName;

        @SerializedName("phone")
        private String phone;

        public User(String firstName, String lastName, String phone) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
        }

        public String getFirstName() {
            return firstName;
        }
        public String getLastName() {
            return lastName;
        }
        public String getPhone() {
            return phone;
        }
    }

    @SerializedName("user")
    private User user;
    @SerializedName("height")
    private double height;
    @SerializedName("weight")
    private double weight;
    @SerializedName("device")
    private String device;
    @SerializedName("treatment_start")
    private String treatment_start;
    @SerializedName("status")
    private String status;

    public PatientInfo(User user, double height, double weight, String device, String treatment_start, String status) {
        this.user = user;
        this.height = height;
        this.weight = weight;
        this.device = device;
        this.treatment_start = treatment_start;
        this.status = status;
    }

    public User getUser() {
        return user;
    }
    public double getHeight() {
        return height;
    }
    public double getWeight() {
        return weight;
    }
    public String getDevice() {
        return device;
    }
    public String getTreatment_start() { return treatment_start; }
    public String getStatus() { return status; }
}
