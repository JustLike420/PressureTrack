package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import android.content.SharedPreferences;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface APIInterface {

    @POST("accounts/auth/token/login/")
    Call<LoginResponse> performLogin(@Body LoginData loginData);

    @GET("patient/profile/")
    Call<PatientProfile> mainLoader(@Header("Authorization") String token);

}
