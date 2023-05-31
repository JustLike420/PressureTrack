package ru.mirea.zhalyaletdinov_f_n.doctorapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface APIInterface {
    @POST("accounts/auth/token/login/")
    Call<LoginResponse> performLogin(@Body LoginData loginData);

    @GET("accounts/profile/")
    Call<AccountProfile> mainLoader(@Header("Authorization") String token);

    @POST("accounts/auth/token/logout/")
    Call<Void> logout(@Header("Authorization") String token);

    @GET("doctor/patients/")
    Call<List<PatientCard>> getPatientList(@Header("Authorization") String token, @Query("status") String status);


}
