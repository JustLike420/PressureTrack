package ru.mirea.zhalyaletdinov_f_n.doctorapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface APIInterface {
    @POST("accounts/auth/token/login/")
    Call<LoginResponse> performLogin(@Body LoginData loginData);

    @GET("accounts/profile/")
    Call<AccountProfile> mainLoader(@Header("Authorization") String token);

    @POST("accounts/auth/token/logout/")
    Call<Void> logout(@Header("Authorization") String token);

    @GET("doctor/patients/")
    Call<List<PatientCard>> getPatientList(@Header("Authorization") String token, @Query("status") String status);

    @GET("doctor/patients/{id}")
    Call<PatientInfo> getPatientInfo(@Header("Authorization") String token, @Path("id") String pk);

    @GET("doctor/measurements/{id}")
    Call<List<GetMeasurment>> getMeasList(@Header("Authorization") String token, @Path("id") String pk);

    @PUT("doctor/change_status/{id}")
    Call<Void> archivePatient(@Header("Authorization") String token, @Path("id") String pk);

    @POST("doctor/treatments/{id}")
    Call<Void> createTreatment(@Header("Authorization") String token, @Path("id") String pk, @Body TreatmentData treatmentData);

     @GET("doctor/treatments/{id}")
     Call<List<TreatmentData>> getTreatments(@Header("Authorization") String token, @Path("id") String pk);
}
