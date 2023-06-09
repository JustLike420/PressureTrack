package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;


import com.google.gson.annotations.SerializedName;

public class GetMeasurment {
    @SerializedName("id")
    private int id;
    @SerializedName("top")
    private int top;
    @SerializedName("bottom")
    private int bottom;
    @SerializedName("pulse")
    private int pulse;
    @SerializedName("comment")
    private String comment;
    @SerializedName("created_at")
    private String created_at;

    public GetMeasurment(int id, int top, int bottom, int pulse, String comment, String created_at) {
        this.id = id;
        this.top = top;
        this.bottom = bottom;
        this.pulse = pulse;
        this.comment = comment;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getPulse() {
        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
