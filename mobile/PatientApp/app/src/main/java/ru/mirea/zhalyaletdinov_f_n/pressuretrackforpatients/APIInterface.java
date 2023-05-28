package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface APIInterface {
    @POST("accounts/auth/token/login/")
    Call<LoginResponse> performLogin(@Body LoginData loginData);

    @GET("patient/profile/")
    Call<PatientProfile> mainLoader(@Header("Authorization") String token);

    @POST("accounts/auth/token/logout/")
    Call<Void> logout(@Header("Authorization") String token);

    @POST("accounts/craete_patient/")
    Call<CreatePatient> createPatient(@Body CreatePatient createPatient);

    @PUT("patient/change_device/")
    Call<PatientProfile> changeDevice(@Header("Authorization") String token, @Body Device device);
}
