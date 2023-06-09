package ru.mirea.zhalyaletdinov_f_n.doctorapp;

import com.google.gson.annotations.SerializedName;

public class AccountProfile {
    @SerializedName("first_name")
    private String name;

    @SerializedName("last_name")
    private String last_name;

    public String getName() { return this.name; }

    public String getLastName() { return this.last_name; }

    public void setName(String name) { this.name = name; }

    public void setLastName(String last_name) { this.last_name = last_name; }
}
